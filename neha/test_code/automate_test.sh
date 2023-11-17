#!/bin/bash

# Read the API key from an environment variable
API_KEY=$OPENAI_API_KEY

# Check if parameter name and method name are provided
if [ $# -ne 2 ]; then
    echo "Usage: $0 <parameter_name> <method_name>"
    exit 1
fi

# Set parameter name and method name variables
PARAMETER_NAME=$1
METHOD_NAME=$2

# package org.apache.hadoop.util;
# Define the license header and package declaration
LICENSE_HEADER="/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * \"License\"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.llmgenerated;"

# Set Hadoop Common project path
HADOOP_COMMON_PATH="/home/nvadde2/hadoop/hadoop-common-project/hadoop-common"
TEST_FILE_PATH="$HADOOP_COMMON_PATH/src/test/java/org/apache/hadoop/llmgenerated/TestHttpServer.java"

# Query GPT-4 for response
PROMPT="Generate a Java unit test method for '$METHOD_NAME' method that tests the configuration parameter '$PARAMETER_NAME'. Please generate code only, no explanations. Also include edge cases and invalid parameter values that detect misconfigurations in the test generated."
#RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" -H "Authorization: Bearer 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" -d '{"prompt": "'"$PROMPT"'", "temperature": 0.7, "max_tokens": 150}' "https://api.openai.com/v1/engines/gpt-4.0-turbo/completions")
JSON_DATA=$(jq -n \
  --arg prompt "Generate a unit test for the method $METHOD_NAME with the configuration parameter $PARAMETER_NAME in Hadoop Common project. Ensure the code is in Java and please provide the code without any explanations or text except code." \
  --arg model "gpt-4" \
  '{
    model: $model,
    messages: [{ "role": "system", "content": "You are a Java Developer." }, { "role": "user", "content": $prompt }],
    temperature: 0,
    max_tokens: 1000
  }')
# echo "JSON payload: $JSON_DATA"

RESPONSE=$(curl -X POST "https://api.openai.com/v1/chat/completions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPENAI_API_KEY" \
  --data "$JSON_DATA")
# echo $RESPONSE

# Extracting the unit test code from the response
UNIT_TEST_CODE=$(echo $RESPONSE | jq -r '.choices[0].message.content')
echo $UNIT_TEST_CODE

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
CLEANED_UNIT_TEST_CODE=$(echo "$UNIT_TEST_CODE" | sed -e 's/^```java//g' -e 's/```$//g')

# Check the cleaned code
# echo "$CLEANED_UNIT_TEST_CODE"
# sed -i '1s/^```java//' "$CLEANED_UNIT_TEST_CODE" # Removes the ```java\n at the beginning of the file
# sed -i '$ s/```$//' "$CLEANED_UNIT_TEST_CODE" # Removes the ``` at the end of the file

# Write the cleaned response to the file, overwriting existing contents

# Add the license header and package declaration to the beginning of the test file
echo "$LICENSE_HEADER$CLEANED_UNIT_TEST_CODE" > "$TEST_FILE_PATH"
#echo "$CLEANED_UNIT_TEST_CODE" > "$TEST_FILE_PATH"

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

# Check if build was successful
# Function to send error to GPT and get response
send_to_gpt() {
    local ERROR_MESSAGE="$1"
    echo "Received error message: $ERROR_MESSAGE"

    local ERROR_PROMPT="The Java unit test failed with the following error: $ERROR_MESSAGE. Please suggest a fix to resolve this error. Provide complete test code without any explanations."
    echo "Constructed prompt: $ERROR_PROMPT"

    local ERROR_JSON=$(jq -n \
        --arg prompt "$ERROR_PROMPT" \
        --arg model "gpt-4" \
        '{
            model: $model,
            messages: [{ "role": "system", "content": "You are a helpful assistant." }, { "role": "user", "content": $prompt }],
                    temperature: 0,
            max_tokens: 1000
        }')
    echo "JSON payload: $ERROR_JSON"

    RESPONSE=$(curl -X POST "https://api.openai.com/v1/chat/completions" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $OPENAI_API_KEY" \
        --data "$ERROR_JSON")

    if [ -z "$RESPONSE" ]; then
        echo "Error: No response from GPT API."
        return 1
    else
        echo "$RESPONSE"
    fi
}

for ATTEMPT in {1..5}; do
    echo "Attempt $ATTEMPT"
    BUILD_OUTPUT=$(mvn clean install -B -DskipTests 2>&1)
    echo "$BUILD_OUTPUT"

    if echo "$BUILD_OUTPUT" | grep -q "BUILD FAILURE"; then
        echo "Build failed. Sending error to GPT..."
        echo "$BUILD_OUTPUT"
        # ERROR_MSG=$(echo "$BUILD_OUTPUT" | sed -n '/\[INFO\] BUILD FAILURE \[INFO\]/,/\[ERROR\] Re-run Maven using the -X switch to enable full debug logging./p' | sed '$d' | jq -aRs .)
        # ERROR_MSG=$(echo "$BUILD_OUTPUT" | grep -q "\[ERROR\](.|\n)*")
        ERROR_MSG=$(echo "$BUILD_OUTPUT" | grep -i "\[ERROR\]")
        echo "Error message: $ERROR_MSG"
        
        echo "Sending extracted error to GPT function..."
        GPT_RESPONSE=$(send_to_gpt "$ERROR_MSG")

        echo "Response received from GPT function:"
        echo "$GPT_RESPONSE"
        echo "$GPT_RESPONSE" | jq '.'

        SUGGESTED_FIX=$(echo "$GPT_RESPONSE" | jq -r '.choices[0].text')
        echo "Suggested fix:"
        echo "$SUGGESTED_FIX"
    else
        echo "Build succeeded."
        break
    fi
done
