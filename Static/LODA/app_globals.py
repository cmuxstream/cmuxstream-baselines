from argparse import ArgumentParser
from r_support import *
from copy import copy

# ==============================
# Detector types
# ------------------------------
SIMPLE_UPD_TYPE = 1
SIMPLE_UPD_TYPE_R_OPTIM = 2
AAD_UPD_TYPE = 3
AAD_SLACK_CONSTR_UPD_TYPE = 4
BASELINE_UPD_TYPE = 5
AAD_ITERATIVE_GRAD_UPD_TYPE = 6
AAD_IFOREST = 7
SIMPLE_PAIRWISE = 8
IFOREST_ORIG = 9
ATGP_IFOREST = 10
AAD_HSTREES = 11
AAD_RSFOREST = 12


# ==============================
# Forest Score Types
# ------------------------------
IFOR_SCORE_TYPE_INV_PATH_LEN = 0
IFOR_SCORE_TYPE_INV_PATH_LEN_EXP = 1
IFOR_SCORE_TYPE_NORM = 2
IFOR_SCORE_TYPE_CONST = 3
IFOR_SCORE_TYPE_NEG_PATH_LEN = 4
HST_SCORE_TYPE = 5
RSF_SCORE_TYPE = 6
RSF_LOG_SCORE_TYPE = 7
ORIG_TREE_SCORE_TYPE = 8

ENSEMBLE_SCORE_LINEAR = 0  # linear combination of scores
ENSEMBLE_SCORE_EXPONENTIAL = 1  # exp(linear combination)
ensemble_score_names = ["linear", "exp"]

# Detector type names - first is blank string so these are 1-indexed
detector_types = ["", "simple_online", "online_optim", "aad",
                "aad_slack", "baseline", "iter_grad", "iforest",
                "simple_pairwise", "iforest_orig", "if_atgp", "hstrees", "rsfor"]
# ------------------------------

# ==============================
# Constraint types when Detector Type is AAD_PAIRWISE_CONSTR_UPD_TYPE
# ------------------------------
AAD_CONSTRAINT_NONE = 0  # no constraints
AAD_CONSTRAINT_PAIRWISE = 1  # slack vars [0, Inf]; weights [-Inf, Inf]
AAD_CONSTRAINT_PAIRWISE_WEIGHTS_POSITIVE_SUM_1 = 2  # slack vars [0, Inf]; weights [0, Inf]
AAD_CONSTRAINT_WEIGHTS_POSITIVE_SUM_1 = 3  # no pairwise; weights [0, Inf], sum(weights)=1
AAD_CONSTRAINT_TAU_INSTANCE = 4  # tau-th quantile instance will be used in pairwise constraints

# Constraint type names - first is blank string so these are 1-indexed
constraint_types = ["no_constraints", "pairwise", "pairwise_pos_wts_sum1", "pos_wts_sum1", "tau_instance"]

# ==============================
# Baseline to use for simple weight inference
# ------------------------------
RELATIVE_MEAN = 1
RELATIVE_QUANTILE = 2

# first is blank to make the name list 1-indexed
RELATIVE_TO_NAMES = ["", "mean", "quantile"]
# ------------------------------


# ==============================
# Query types
# ------------------------------
QUERY_DETERMINISIC = 1
QUERY_BETA_ACTIVE = 2
QUERY_QUANTILE = 3
QUERY_RANDOM = 4
QUERY_SEQUENTIAL = 5
QUERY_GP = 6
QUERY_SCORE_VAR = 7

# first blank string makes the other names 1-indexed
query_type_names = ["", "top", "beta_active", "quantile", "random", "sequential", "gp", "scvar"]
# ------------------------------


# ==============================
# Optimization libraries
# ------------------------------
OPTIMLIB_SCIPY = 'scipy'
OPTIMLIB_CVXOPT = 'cvxopt'
# ------------------------------


def get_option_list():
    parser = ArgumentParser()
    parser.add_argument("--filedir", action="store", default="",
                        help="Folder for input files")
    parser.add_argument("--cachedir", action="store", default="",
                        help="Folder where the generated models will be cached for efficiency")
    parser.add_argument("--plotsdir", action="store", default="",
                        help="Folder for output plots")
    parser.add_argument("--resultsdir", action="store", default="",
                        help="Folder where the generated metrics will be stored")
    parser.add_argument("--header", action="store_true", default=False,
                        help="Whether input file has header row")
    parser.add_argument("--startcol", action="store", type=int, default=2,
                        help="Starting column (1-indexed) for data in input CSV")
    parser.add_argument("--labelindex", action="store", type=int, default=1,
                        help="Index of the label column (1-indexed) in the input CSV. Lables should be anomaly/nominal")
    parser.add_argument("--dataset", action="store", default="", required=False,
                        help="Which dataset to use")
    parser.add_argument("--maxk", action="store", type=int, default=200,
                        help="Maximum number of random projections for LODA")
    parser.add_argument("--original_dims", action="store_true", default=False,
                        help="Whether to use original feature space instead of random projections")
    parser.add_argument("--randseed", action="store", type=int, default=42,
                        help="Random seed so that results can be replicated")
    parser.add_argument("--querytype", action="store", type=int, default=QUERY_DETERMINISIC,
                        help="Query strategy to use. 1 - Top, 2 - Beta-active, 3 - Quantile, 4 - Random")
    parser.add_argument("--reps", action="store", type=int, default=0,
                        help="Number of independent dataset samples to use")
    parser.add_argument("--reruns", action="store", type=int, default=0,
                        help="Number of times each sample dataset should be rerun with randomization")
    parser.add_argument("--runtype", action="store", type=str, default="simple",
                        help="[simple|multi] Whether the there are multiple sub-samples for the input dataset")
    parser.add_argument("--budget", action="store", type=int, default=35,
                        help="Number of feedback iterations")
    parser.add_argument("--maxbudget", action="store", type=int, default=100,
                        help="Maximum number of feedback iterations")
    parser.add_argument("--topK", action="store", type=int, default=0,
                        help="Top rank within which anomalies should be present")
    parser.add_argument("--tau", action="store", type=float, default=0.03,
                        help="Top quantile within which anomalies should be present. "
                             "Relevant only when topK<=0")
    parser.add_argument("--tau_nominal", action="store", type=float, default=0.5,
                        help="Top quantile below which nominals should be present. "
                             "Relevant only when simple quantile inference is used")
    parser.add_argument("--withprior", action="store_true", default=False,
                        help="Whether to use weight priors")
    parser.add_argument("--unifprior", action="store_true", default=False,
                        help="Whether to use uniform priors for weights. "
                             "By default, weight from previous iteration "
                             "is used as prior when --withprior is specified.")
    parser.add_argument("--batch", action="store_true", default=False,
                        help="Whether to query by active learning or select top ranked based on uniform weights")
    parser.add_argument("--sigma2", action="store", type=float, default=0.5,
                        help="If prior is used on weights, then the variance of prior")
    parser.add_argument("--Ca", action="store", type=float, default=100.,
                        help="Penalty for anomaly")
    parser.add_argument("--Cn", action="store", type=float, default=1.,
                        help="Penalty on nominals")
    parser.add_argument("--Cx", action="store", type=float, default=1000.,
                        help="Penalty on constraints")
    parser.add_argument("--detector_type", action="store", type=int, default=AAD_UPD_TYPE,
                        help="Inference algorithm (simple_online(1) / online_optim(2) / aad_pairwise(3))")
    parser.add_argument("--constrainttype", action="store", type=int, default=AAD_CONSTRAINT_PAIRWISE,
                        help="Inference algorithm (simple_online(1) / online_optim(2) / aad_pairwise(3))")
    parser.add_argument("--orderbyviolated", action="store_true", default=False,
                        help="Order by degree of violation when selecting subset of instances for constraints.")
    parser.add_argument("--ignoreAATPloss", action="store_true", default=False,
                        help="Ignore the AATP hinge loss in optimization function.")
    parser.add_argument("--random_instance_at_start", action="store_true", default=False,
                        help="[EXPERIMENTAL] Use random instance as tau-th instance in the first feedback.")
    parser.add_argument("--pseudoanomrank_always", action="store_true", default=False,
                        help="Whether to always use pseudo anomaly instance")
    parser.add_argument("--max_anomalies_in_constraint_set", type=int, default=1000, required=False,
                        help="Maximum number of labeled anomaly instances to use in building pair-wise constraints")
    parser.add_argument("--max_nominals_in_constraint_set", type=int, default=1000, required=False,
                        help="Maximum number of labeled nominal instances to use in building pair-wise constraints")
    parser.add_argument("--relativeto", action="store", type=int, default=RELATIVE_MEAN,
                        help="The relative baseline for simple online (1=mean, 2=quantile)")
    parser.add_argument("--query_search_candidates", action="store", type=int, default=1,
                        help="Number of search candidates to use in each search state (when query_type=5)")
    parser.add_argument("--query_search_depth", action="store", type=int, default=1,
                        help="Depth of search tree (when query_type=5)")
    parser.add_argument("--debug", action="store_true", default=False,
                        help="Whether to enable output of debug statements")
    parser.add_argument("--log_file", type=str, default="", required=False,
                        help="File path to debug logs")
    parser.add_argument("--optimlib", type=str, default=OPTIMLIB_SCIPY, required=False,
                        help="optimization library to use")
    parser.add_argument("--op", type=str, default="nop", required=False,
                        help="name of operation")
    parser.add_argument("--cachetype", type=str, default="pydata", required=False,
                        help="type of cache (csv|pydata)")

    parser.add_argument("--scoresdir", type=str, default="", required=False,
                        help="Folder where precomputed scores from ensemble of detectors are stored in CSV format. "
                        "Applies only when runtype=simple")

    parser.add_argument("--ensembletype", type=str, default="regular", required=False,
                        help="[regular|loda] - 'regular' if the file has precomputed scores from ensembles; "
                             "'loda' if LODA projections are to be used as ensemble members. Note: LODA is stochastic, "
                             "hence multiple runs might be required to get an average estimate of accuracy.")
    parser.add_argument("--datafile", type=str, default="", required=False,
                        help="Original data in CSV format. This is used when runtype is 'regular'")
    parser.add_argument("--scoresfile", type=str, default="", required=False,
                        help="Precomputed scores from ensemble of detectors in CSV format. One detector per column;"
                             "first column has label [anomaly|nominal]")

    parser.add_argument("--ifor_n_trees", action="store", type=int, default=100,
                        help="Number of trees for Isolation Forest")
    parser.add_argument("--ifor_n_samples", action="store", type=int, default=256,
                        help="Number of samples to build each tree in Isolation Forest")
    parser.add_argument("--ifor_score_type", action="store", type=int, default=IFOR_SCORE_TYPE_CONST,
                        help="Type of anomaly score computation for a node in Isolation Forest")
    parser.add_argument("--ifor_add_leaf_nodes_only", action="store_true", default=False,
                        help="Whether to include only leaf node regions only or intermediate node regions as well.")
    parser.add_argument("--modelfile", action="store", default="",
                        help="Model file path in case the model needs to be saved or loaded. Supported only for Isolation Forest.")
    parser.add_argument("--save_model", action="store_true", default=False,
                        help="Whether to save the trained model")
    parser.add_argument("--load_model", action="store_true", default=False,
                        help="Whether to load a pre-trained model")

    parser.add_argument("--plot2D", action="store_true", default=False,
                        help="Whether to plot the data, trees, and countours. Only supported for 2D data")

    parser.add_argument("--n_jobs", action="store", type=int, default=1,
                        help="Number of parallel threads (if supported)")

    parser.add_argument("--forest_n_trees", action="store", type=int, default=100,
                        help="Number of trees for Forest")
    parser.add_argument("--forest_n_samples", action="store", type=int, default=256,
                        help="Number of samples to build each tree in Forest")
    parser.add_argument("--forest_score_type", action="store", type=int, default=IFOR_SCORE_TYPE_CONST,
                        help="Type of anomaly score computation for a node in Forest")
    parser.add_argument("--ensemble_score", action="store", type=int, default=ENSEMBLE_SCORE_LINEAR,
                        help="How to combine scores from ensemble members")
    parser.add_argument("--forest_add_leaf_nodes_only", action="store_true", default=False,
                        help="Whether to include only leaf node regions only or intermediate node regions as well.")
    parser.add_argument("--forest_max_depth", action="store", type=int, default=15,
                        help="Number of samples to build each tree in Forest")

    parser.add_argument("--n_explore", action="store", type=int, default=20,
                        help="Number of top ranked instances to evaluate during exploration (query types GP and score variance)")

    parser.add_argument("--streaming", action="store_true", default=False,
                        help="Whether to run the algorithm in streaming setting")
    parser.add_argument("--stream_window", action="store", type=int, default=512,
                        help="Number of instances to hold in buffer before updating the model")
    parser.add_argument("--allow_stream_update", action="store_true", default=False,
                        help="Update the model when the window buffer is full in the streaming setting")
    return parser


def get_command_args(debug=False, debug_args=None):
    parser = get_option_list()

    if debug:
        unparsed_args = debug_args
    else:
        unparsed_args = sys.argv
        if len(unparsed_args) > 0:
            unparsed_args = unparsed_args[1:len(unparsed_args)]  # script name is first arg

    args = parser.parse_args(unparsed_args)

    if args.startcol < 1:
        raise ValueError("startcol is 1-indexed and must be greater than 0")
    if args.labelindex < 1:
        raise ValueError("labelindex is 1-indexed and must be greater than 0")

    # LODA arguments
    args.keep = None
    args.exclude = None
    args.sparsity = np.nan
    args.explain = False
    #args.ntop = 30 # only for explanations
    args.marked = []

    return args


class Opts(object):
    def __init__(self, args):
        self.use_rel = False
        self.minfid = min(1, args.reps)
        self.maxfid = args.reps
        self.reruns = args.reruns
        self.runtype = args.runtype
        self.budget = args.budget
        self.maxbudget = args.maxbudget
        self.original_dims = args.original_dims
        self.qtype = args.querytype
        self.thres = 0.0  # used for feature weight in projection vector
        self.gam = 0.0  # used for correlation between projections
        self.nu = 1.0
        self.Ca = args.Ca  # 100.0,
        self.Cn = args.Cn
        self.Cx = args.Cx  # penalization for slack in pairwise constraints
        self.topK = args.topK
        self.tau = args.tau
        self.detector_type = args.detector_type
        self.constrainttype = args.constrainttype
        self.ignoreAATPloss = args.ignoreAATPloss
        self.orderbyviolated = args.orderbyviolated
        self.withprior = args.withprior  # whether to include prior in loss
        self.unifprior = args.unifprior
        self.priorsigma2 = args.sigma2  # 0.2, #0.5, #0.1,
        self.single_inst_feedback = False
        self.batch = args.batch
        self.random_instance_at_start = args.random_instance_at_start
        self.pseudoanomrank_always = args.pseudoanomrank_always
        self.max_anomalies_in_constraint_set = args.max_anomalies_in_constraint_set
        self.max_nominals_in_constraint_set = args.max_nominals_in_constraint_set
        self.precision_k = [10, 20, 30]
        self.plot_hist = False
        self.relativeto = args.relativeto
        self.tau_nominal = args.tau_nominal
        self.query_search_candidates = args.query_search_candidates
        self.query_search_depth = args.query_search_depth
        self.optimlib = args.optimlib
        self.exclude = None
        self.keep = args.keep

        self.randseed = args.randseed

        # LODA specific
        self.mink = 100
        self.maxk = max(self.mink, args.maxk)
        self.sparsity = args.sparsity

        # file related options
        self.dataset = args.dataset
        self.header = args.header
        self.startcol = args.startcol
        self.filedir = args.filedir
        self.cachedir = args.cachedir
        self.resultsdir = args.resultsdir
        self.cachetype = args.cachetype
        self.fid = -1
        self.runidx = -1

        self.ensembletype = args.ensembletype
        self.datafile = args.datafile
        self.scoresdir = args.scoresdir
        self.scoresfile = args.scoresfile

        self.ifor_n_trees = args.ifor_n_trees
        self.ifor_n_samples = args.ifor_n_samples
        self.ifor_score_type = args.ifor_score_type
        self.ifor_add_leaf_nodes_only = args.ifor_add_leaf_nodes_only

        self.plot2D = args.plot2D
        self.n_jobs = args.n_jobs

        self.forest_n_trees = args.forest_n_trees
        self.forest_n_samples = args.forest_n_samples
        self.forest_score_type = args.forest_score_type
        self.forest_add_leaf_nodes_only = args.forest_add_leaf_nodes_only
        self.forest_max_depth = args.forest_max_depth

        self.n_explore = args.n_explore

        self.ensemble_score = args.ensemble_score

        self.streaming = args.streaming
        self.stream_window = args.stream_window
        self.allow_stream_update = args.allow_stream_update

        self.modelfile = args.modelfile
        self.load_model = args.load_model
        self.save_model = args.save_model

    def is_simple_run(self):
        return self.runtype == "simple"

    def get_fids(self):
        if self.is_simple_run():
            return [0]
        else:
            return range(self.minfid, self.maxfid + 1)

    def get_runidxs(self):
        if self.is_simple_run():
            return [0]
        else:
            return range(1, self.reruns + 1)

    def set_multi_run_options(self, fid, runidx):
        self.fid = fid
        self.runidx = runidx

    def query_name_str(self):
        s = query_type_names[self.qtype]
        if self.qtype == QUERY_SEQUENTIAL:
            s = "%s_nc%d_d%d" % (s, self.query_search_candidates, self.query_search_depth)
        return s

    def streaming_str(self):
        return "sw%d_asu%s" % (self.stream_window, str(self.allow_stream_update))

    def detector_type_str(self):
        s = detector_types[self.detector_type]
        if self.detector_type == AAD_UPD_TYPE:
            return "%s_%s" % (s, constraint_types[self.constrainttype])
        elif (self.detector_type == AAD_IFOREST or self.detector_type == ATGP_IFOREST or
                self.detector_type == AAD_HSTREES or self.detector_type == AAD_RSFOREST):
            return "%s_%s-trees%d_samples%d_nscore%d%s" % \
                   (s, constraint_types[self.constrainttype],
                    self.forest_n_trees, self.forest_n_samples, self.forest_score_type,
                    "_leaf" if self.forest_add_leaf_nodes_only else "")
        else:
            return s

    def model_file_prefix(self):
        return "%s_%d_r%d" % (self.dataset, self.fid, self.runidx)

    def get_metrics_path(self):
        prefix = self.get_alad_metrics_name_prefix()
        return os.path.join(self.resultsdir, prefix + "_alad_metrics.pydata")

    def get_metrics_summary_path(self):
        prefix = self.get_alad_metrics_name_prefix()
        return os.path.join(self.resultsdir, prefix + "_alad_summary.pydata")

    def get_alad_metrics_name_prefix(self):
        if not self.is_simple_run():
            filesig = ("-fid%d" % (self.fid,)) + ("-runidx%d" % (self.runidx,))
        else:
            filesig = ""
        optimsig = "-optim_%s" % (self.optimlib,)
        orderbyviolatedsig = "-by_violated" if self.orderbyviolated else ""
        ignoreAATPlosssig = "-noAATP" if self.ignoreAATPloss else ""
        randomInstanceAtStartSig = "-randFirst" if self.random_instance_at_start else ""
        streaming_sig = "-" + self.streaming_str() if self.streaming else ""
        nameprefix = (self.dataset +
                      ("-" + self.detector_type_str()) +
                      ("_" + RELATIVE_TO_NAMES[self.relativeto] if self.detector_type == SIMPLE_UPD_TYPE else "") +
                      randomInstanceAtStartSig +
                      ("-single" if self.single_inst_feedback else "") +
                      ("-" + self.query_name_str()) +
                      ("-orig" if self.original_dims else "") +
                      ("-batch" if self.batch else "-active") +
                      (("-unifprior" if self.unifprior else "-prevprior" + str(
                          self.priorsigma2)) if self.withprior else "-noprior") +
                      # ("-with_meanrel" if opts.withmeanrelativeloss else "-no_meanrel") +
                      ("-Ca%.0f" % (self.Ca,)) +
                      (("-Cn%0.0f" % (self.Cn,)) if self.Cn != 1 else "") +
                      ("-%d_%d" % (self.minfid, self.maxfid)) +
                      filesig +
                      ("-bd%d" % (self.budget,)) +
                      ("-tau%0.3f" % (self.tau,)) +
                      ("-tau_nominal" if self.detector_type == SIMPLE_UPD_TYPE
                                         and self.relativeto == RELATIVE_QUANTILE
                                         and self.tau_nominal != 0.5 else "") +
                      ("-topK%d" % (self.topK,)) +
                      ("-pseudoanom_always_%s" % (self.pseudoanomrank_always,)) +
                      optimsig +
                      orderbyviolatedsig +
                      ignoreAATPlosssig +
                      streaming_sig
                      )
        return nameprefix.replace(".", "_")

    def cached_loda_projections_path(self):
        """pre-computed cached projections path"""
        return os.path.join(self.cachedir, 'loda_projs')

    def str_opts(self):
        orderbyviolatedsig = "-by_violated" if self.orderbyviolated else ""
        ignoreAATPlosssig = "-noAATP" if self.ignoreAATPloss else ""
        randomInstanceAtStartSig = "-randFirst" if self.random_instance_at_start else ""
        streaming_sig = "-" + self.streaming_str() if self.streaming else ""
        srr = (("[" + self.dataset + "]") +
               ("-%s" % (self.detector_type_str(),)) +
               (("_%s" % (RELATIVE_TO_NAMES[self.relativeto],)) if self.detector_type == SIMPLE_UPD_TYPE else "") +
               randomInstanceAtStartSig +
               ("-single" if self.single_inst_feedback else "") +
               ("-query_" + self.query_name_str()) +
               ("-orig" if self.original_dims else "") +
               ("-batch" if self.batch else "-active") +
               ((("-unifprior" if self.unifprior else "-prevprior") + str(self.priorsigma2))
                if self.withprior else "-noprior") +
               ("-Ca" + str(self.Ca)) +
               (("-Cn" + str(self.Cn)) if self.Cn != 1 else "") +
               (("-Cx" + str(self.Cx)) if self.Cx != 1 else "") +
               ("-" + str(self.minfid) + "_" + str(self.maxfid)) +
               ("-reruns" + str(self.reruns)) +
               ("-bd" + str(self.budget)) +
               ("-tau" + str(self.tau)) +
               ("-tau_nominal" if self.detector_type == SIMPLE_UPD_TYPE
                                  and self.relativeto == RELATIVE_QUANTILE
                                  and self.tau_nominal != 0.5 else "") +
               ("-topK" + str(self.topK)) +
               ("-pseudoanom_always_" + str(self.pseudoanomrank_always)) +
               ("-orgdim" if self.original_dims else "") +
               ("sngl_fbk" if self.single_inst_feedback else "") +
               ("-optimlib_%s" % (self.optimlib,)) +
               orderbyviolatedsig +
               ignoreAATPlosssig +
               streaming_sig
               )
        return srr


def get_first_val_not_marked(vals, marked, start=1):
    for i in range(start, len(vals)):
        f = vals[i]
        if len(np.where(marked == f)[0]) == 0:
            return f
    return None


def get_first_vals_not_marked(vals, marked, n=1, start=1):
    unmarked = []
    for i in range(start, len(vals)):
        f = vals[i]
        if len(np.where(marked == f)[0]) == 0:
            unmarked.append(f)
        if len(unmarked) >= n:
            break
    return unmarked


def get_anomalies_at_top(scores, lbls, K):
    ordered_idxs = order(scores)
    sorted_lbls = lbls[ordered_idxs]
    counts = np.zeros(len(K))
    for i in range(len(K)):
        counts[i] = np.sum(sorted_lbls[1:K[i]])
    return counts


class SampleData(object):
    def __init__(self, lbls, fmat, fid):
        self.lbls = lbls
        self.fmat = fmat
        self.fid = fid


def load_samples(filepath, opts, fid=-1):
    """Loads the data file.

    :param filepath: str
    :param opts: Opts
    :param fid: int
    :return: SampleData
    """
    fdata = read_csv(filepath, header=opts.header)
    fmat = np.ndarray(shape=(fdata.shape[0], fdata.shape[1] - opts.startcol + 1), dtype=float)
    fmat[:, :] = fdata.iloc[:, (opts.startcol - 1):fdata.shape[1]]
    lbls = np.array([1 if v == "anomaly" else 0 for v in fdata.iloc[:, 0]], dtype=int)
    return SampleData(lbls=lbls, fmat=fmat, fid=fid)


def load_all_samples(dataset, dirpath, fids, opts):
    """
    Args:
        dataset:
        dirpath:
        fids:
        opts:
            opts.startcol: 1-indexed column number
            opts.labelindex: 1-indexed column number
    Returns: list
    """
    alldata = []
    for fid in fids:
        filename = "%s_%d.csv" % (dataset, fid)
        filepath = os.path.join(dirpath, filename)
        alldata.append(load_samples(filepath, opts, fid=fid))
    return alldata


