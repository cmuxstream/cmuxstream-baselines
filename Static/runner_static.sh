in_file=$1
out_dir=$2
num_runs=$3
num_comps=$4

cd iForest
echo "Running iForest"
python iForest.py $in_file $out_dir"/IF_sample_data" "$num_runs" "$num_comps"

cd ../HSTrees
echo "Running HST"
python HSTree_runner.py $in_file $out_dir"/HST_sample_data" $num_runs 1 15

cd ../RS_Hash
echo "Running RSH"
python RSHash.py $in_file $out_dir"/RSH_sample_data" $num_runs $num_comps 1000

cd ../LODA
echo "Running LODA"
python loda_runner.py $in_file $out_dir"/LODA_sample_data" $num_runs
