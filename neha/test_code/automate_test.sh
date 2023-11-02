#!/bin/bash

# Read the API key from an environment variable
API_KEY="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"

# Check if parameter name and method name are provided
if [ $# -ne 2 ]; then
    echo "Usage: $0 <parameter_name> <method_name>"
    exit 1
fi

# Set parameter name and method name variables
PARAMETER_NAME=$1
METHOD_NAME=$2

# Set Hadoop Common project path
HADOOP_COMMON_PATH="/home/nvadde2/hadoop/hadoop-common-project/hadoop-common"
TEST_FILE_PATH="$HADOOP_COMMON_PATH/src/test/java/org/apache/hadoop/conf/TestConfiguration.java"

# response = openai.ChatCompletion.create( model="gpt-4", messages=[{"role": "user", "content": "Generate a 3 sentence story about friendship"}] ) 
# print(response)
# print(response.choices[0].message.content)

# Query GPT-4 for response
PROMPT="Generate a Java unit test method for '$METHOD_NAME' method that tests the configuration parameter '$PARAMETER_NAME'. Please generate code only, no explanations."
#RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" -H "Authorization: Bearer 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" -d '{"prompt": "'"$PROMPT"'", "temperature": 0.7, "max_tokens": 150}' "https://api.openai.com/v1/engines/gpt-4.0-turbo/completions")
JSON_DATA=$(jq -n \
  --arg prompt "Generate a unit test for the method $METHOD_NAME with the configuration parameter $PARAMETER_NAME in Hadoop Common project. Ensure the code is in Java and please provide the code without any explanations." \
  --arg model "gpt-4" \
  '{
    model: $model,
    messages: [{ "role": "system", "content": "You are a helpful assistant." }, { "role": "user", "content": $prompt }],
    temperature: 0,
    max_tokens: 1000
  }')
echo "JSON payload: $JSON_DATA"

RESPONSE=$(curl -X POST "https://api.openai.com/v1/chat/completions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer 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" \
  --data "$JSON_DATA")
echo $RESPONSE



# Extracting the unit test code from the response
UNIT_TEST_CODE=$(echo $RESPONSE | jq -r '.choices[0].message.content')
echo $UNIT_TEST_CODE

# Check if we got a valid response
if [ -z "$UNIT_TEST_CODE" ]; then
    echo "Failed to generate unit test code. Exiting."
    exit 1
fi

# Backup existing test file
cp "$TEST_FILE_PATH" "$TEST_FILE_PATH.bak"

# Inject response into test file
sed -i "/RESPONSE_PLACEHOLDER/i $UNIT_TEST_CODE" "$TEST_FILE_PATH"

# Build Hadoop Common project
cd "$HADOOP_COMMON_PATH" || exit
mvn clean install -DskipTests

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "Build failed. Restoring original test file and exiting."
    mv "$TEST_FILE_PATH.bak" "$TEST_FILE_PATH"
    exit 1
fi

# Run test case
mvn test -Dtest=TestConfiguration

