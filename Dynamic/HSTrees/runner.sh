cd Stochastic/SRC
#javac -cp sizeofag.jar stochastic/*.java
echo "Running Streaming HS-Trees"
java stochastic/Main "4003" "0" "../../../../Sample_Output/HSStream_" "15" "20" "20" "100" "../../../../Data/" "-1000" "1"
