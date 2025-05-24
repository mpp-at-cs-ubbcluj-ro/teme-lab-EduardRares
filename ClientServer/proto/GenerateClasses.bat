
echo "Generating Java classes"
protoc -I=. --java_out=javaFiles --csharp_out=csharpFiles AppProtocol_v3.proto
