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

# package org.apache.hadoop.util;
# Set Hadoop Common project path
HADOOP_COMMON_PATH="/home/nvadde2/hadoop/hadoop-common-project/hadoop-common"
TEST_FILE_PATH="$HADOOP_COMMON_PATH/src/test/java/org/apache/hadoop/util/LineReaderTest.java"

# Query GPT-4 for response
PROMPT="Generate a Java unit test method for '$METHOD_NAME' method that tests the configuration parameter '$PARAMETER_NAME'. Please generate code only, no explanations."
#RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" -H "Authorization: Bearer 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" -d '{"prompt": "'"$PROMPT"'", "temperature": 0.7, "max_tokens": 150}' "https://api.openai.com/v1/engines/gpt-4.0-turbo/completions")
JSON_DATA=$(jq -n \
  --arg prompt "Generate a unit test for the method $METHOD_NAME with the configuration parameter $PARAMETER_NAME in Hadoop Common project. Ensure the code is in Java and please provide the code without any explanations or text except code." \
  --arg model "gpt-4" \
  '{
    model: $model,
    messages: [{ "role": "system", "content": "You are a helpful assistant." }, { "role": "user", "content": $prompt }],
    temperature: 0,
    max_tokens: 1000
  }')
# echo "JSON payload: $JSON_DATA"

RESPONSE=$(curl -X POST "https://api.openai.com/v1/chat/completions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer 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" \
  --data "$JSON_DATA")
# echo $RESPONSE

# Extracting the unit test code from the response
UNIT_TEST_CODE=$(echo $RESPONSE | jq -r '.choices[0].message.content')
# echo $UNIT_TEST_CODE

# Check if we got a valid response
if [ -z "$UNIT_TEST_CODE" ]; then
    echo "Failed to generate unit test code. Exiting."
    exit 1
fi

# Check if the directory for the test file exists, create if not
if [ ! -d "$(dirname "$TEST_FILE_PATH")" ]; then
    mkdir -p "$(dirname "$TEST_FILE_PATH")"
    mkdir -p "$(dirname "$TEST_FILE_PATH").bak"
fi

# Check if the test file exists, create if not
if [ ! -f "$TEST_FILE_PATH" ]; then
    touch "$TEST_FILE_PATH"
    touch "$TEST_FILE_PATH.bak"
fi

# Remove backticks and the word 'java' from the beginning of the unit test code
CLEANED_UNIT_TEST_CODE=$(echo "$UNIT_TEST_CODE" | sed 's/^```java//' | sed 's/```$//')

# Check the cleaned code
echo "$CLEANED_UNIT_TEST_CODE"
# sed -i '1s/^```java//' "$CLEANED_UNIT_TEST_CODE" # Removes the ```java\n at the beginning of the file
# sed -i '$ s/```$//' "$CLEANED_UNIT_TEST_CODE" # Removes the ``` at the end of the file

# Write the cleaned response to the file, overwriting existing contents
echo "$CLEANED_UNIT_TEST_CODE" > "$TEST_FILE_PATH"

# Backup existing test file
cp "$TEST_FILE_PATH" "$TEST_FILE_PATH.bak"

# Inject response into test file using '|' as the delimiter for sed
sed -i "/RESPONSE_PLACEHOLDER/i |$UNIT_TEST_CODE|" "$TEST_FILE_PATH"


# Build Hadoop Common project
cd "$HADOOP_COMMON_PATH" || exit
# mvn clean install -DskipTests

# Check if build was successful
# if [ $? -ne 0 ]; then
#     echo "Build failed. Restoring original test file and exiting."
#     mv "$TEST_FILE_PATH.bak" "$TEST_FILE_PATH"
#     exit 1
# fi

# Run test case
# mvn test -Dtest=org.apache.hadoop.util.LineReaderTest

# Run test case and capture error message if any
# TEST_OUTPUT=$(mvn test -Dtest=org.apache.hadoop.util.LineReaderTest -DfailIfNoTests=false 2>&1)
BUILD_OUTPUT=$(mvn clean install -DskipTests 2>&1)

ERROR_COUNT=0
while [[ $BUILD_OUTPUT == *"[ERROR]"* ]]; do
  echo "Error detected in test run. Asking GPT for help..."
  
  # Extract error message
  ERROR_MSG=$(echo "$BUILD_OUTPUT" | grep "[ERROR]" -A 3 | tail -n 1)
  
  # Ask GPT-3 for help
  PROMPT="The  Java unit test you provided failed with the error: $ERROR_MSG. Please suggest a fix to resolve this error. Provide complete test code without any explanations. "

  JSON_DATA = $(jq -n \
  --arg prompt "The  Java unit test you provided failed with the error: $ERROR_MSG. Please suggest a fix to resolve this error. Provide complete test code without any explanations. " \
  --arg model "gpt-4" \
  '{
    model: $model,
    prompt: $prompt,
    temperature: 0,
    max_tokens: 1000
  }')
  # echo "JSON payload: $JSON_DATA"

  RESPONSE=$(curl -X POST "https://api.openai.com/v1/engines/gpt-4.0-turbo/completions" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer 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" \
    --data "$JSON_DATA")
  # echo $RESPONSE


  # Extract suggested fix from GPT-4 response
  SUGGESTED_FIX=$(echo $RESPONSE | jq -r '.choices[0].message.content')

  # Check if we got a valid response
  if [ -z "$SUGGESTED_FIX" ]; then
    echo "Failed to generate suggested fix. Exiting."
    exit 1
  fi

  # Backup existing test file
  cp "$TEST_FILE_PATH" "$TEST_FILE_PATH.bak"

  
  # Inject suggested fix into test file
  sed -i "/RESPONSE_PLACEHOLDER/i $SUGGESTED_FIX" "$TEST_FILE_PATH"

  # Build Hadoop Common project
  cd "$HADOOP_COMMON_PATH" || exit
  mvn clean install -DskipTests

  # Check if build was successful
  if [ $? -ne 0 ]; then
    echo "Build failed. Restoring original test file and exiting."
    mv "$TEST_FILE_PATH.bak" "$TEST_FILE_PATH"
    exit 1
  fi


  # Re-run test
  TEST_OUTPUT=$(mvn test -Dtest=TestConfiguration -DfailIfNoTests=false 2>&1)

  ERROR_COUNT=$((ERROR_COUNT+1))
  if [ $ERROR_COUNT -eq 10 ]; then
    echo "Failed to resolve test errors after $ERROR_COUNT attempts. Giving up."
    break
  fi
done

# Check if we resolved the errors
if [[ ! $TEST_OUTPUT == *"[ERROR]"* ]]; then
  echo "Test passed after $ERROR_COUNT retries."
else
  echo "Failed to resolve test errors after $ERROR_COUNT attempts. Test output: "
  echo "$TEST_OUTPUT"
fi

# Restore original test file
mv "$TEST_FILE_PATH.bak" "$TEST_FILE_PATH"

