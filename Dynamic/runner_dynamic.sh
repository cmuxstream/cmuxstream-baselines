in_file=$1
out_dir=$2

cd HSTrees
echo "Running HS-Trees"
sh runner.sh

cd ../RS_Hash
echo "Running RSH"
python sparse_stream_RSHash.py ../../Data/http10K.csv ../../Sample_Output/Stream_RSH_http10k.txt 0.015