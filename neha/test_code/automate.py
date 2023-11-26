import os
import re
import sys
import openai
import subprocess

# Set environment variables
openai.api_key = os.environ["OPENAI_API_KEY"]
# OPENAI_API_KEY = os.environ.get('OPENAI_API_KEY')
# print(OPENAI_API_KEY)
# print(openai.api_key)

# Global variable to keep track of the conversation with GPT-3
conversation_history = []

# Function to check input parameters
def check_input_params():
    if len(sys.argv) != 3:
        print("Usage: python script.py <parameter_name> <method_name>")
        sys.exit(1)

def append_to_conversation(role, content):
    global conversation_history
    conversation_history.append({"role": role, "content": content})

def write_test_code_to_file(test_file_path, test_code):
    # Define the license header and package declaration
    LICENSE_HEADER = """/**
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
    package org.apache.hadoop.llmgenerated;"""

    # Remove '```java' and '```' from the response if present
    test_code = test_code.replace("```java", "").replace("```", "").strip()
    
    # Write unit test code to the file
    with open(test_file_path, 'w') as test_file:
        test_file.write(LICENSE_HEADER + "\n\n" + test_code)    

# Function to generate unit test code using OpenAI API
def generate_unit_test(parameter_name, method_name):

    # Define the system message
    system_msg = "You are a skilled Java developer familiar with the Apache Hadoop project. Hadoop is an open-source framework that allows for the distributed processing of large data sets across clusters of computers using simple programming models. It is designed to scale up from single servers to thousands of machines, each offering local computation and storage. Hadoop's codebase is mainly in Java and adheres to Java development best practices. Your task is to generate unit tests for Java methods in the Hadoop project, ensuring the tests are comprehensive and cover various scenarios, including edge cases. The tests should follow Java coding standards and practices suitable for a large-scale, well-maintained open-source project."

    # Define the user message
    user_msg = f"Generate a unit test for the method {method_name} with the configuration parameter {parameter_name} in Hadoop Common project. Ensure the code is in Java and please provide the code without any explanations or text except code."

    # Make sure to append system and user messages to the conversation
    append_to_conversation("system", system_msg)
    append_to_conversation("user", user_msg)

    # Create a dataset using GPT
    response = openai.ChatCompletion.create(model="gpt-4",
                                            messages=[
                                                {
                                                    "role": "system", 
                                                    "content": system_msg
                                                },
                                                {
                                                    "role": "user", 
                                                    "content": user_msg
                                                }]
                                            )
   # Check if the finish reason is 'stop' indicating complete output
    if response["choices"][0]["finish_reason"] == "stop":
        unit_test_code = response["choices"][0]["message"]["content"]

        # Remove '```java' and '```' from the response if present
        unit_test_code = unit_test_code.replace("```java", "").replace("```", "").strip()

        return unit_test_code
    else:
        return None
     
# Function to send error message to GPT
def send_to_gpt(error_msg, unit_test_code):
    system_msg = "You are a skilled Java developer familiar with the Apache Hadoop project. Hadoop is an open-source framework that allows for the distributed processing of large data sets across clusters of computers using simple programming models. It is designed to scale up from single servers to thousands of machines, each offering local computation and storage. Hadoop's codebase is mainly in Java and adheres to Java development best practices. Your task is to generate unit tests for Java methods in the Hadoop project, ensuring the tests are comprehensive and cover various scenarios, including edge cases. The tests should follow Java coding standards and practices suitable for a large-scale, well-maintained open-source project."
    user_msg = f"The Java unit test {unit_test_code} failed with the following error: {error_msg}. Please suggest a fix to resolve this error. Provide complete test code without any explanations or text except code."

     # Append the system message only once, at the start of the conversation
    if not conversation_history:  # If the conversation history is empty, append the system message
        conversation_history.append(system_msg)

    # Always append the latest user message
    conversation_history.append(user_msg)

    # Create a dataset using GPT
    response = openai.ChatCompletion.create(model="gpt-4",
                                            messages=[
                                                {
                                                    "role": "system", 
                                                    "content": system_msg
                                                },
                                                {
                                                    "role": "user", 
                                                    "content": user_msg
                                                }]
                                            # messages=conversation_history
                                            )
   # Check if the finish reason is 'stop' indicating complete output
    if response["choices"][0]["finish_reason"] == "stop":
        fixed_test_code = response["choices"][0]["message"]["content"]

        # Remove '```java' and '```' from the response if present
        fixed_test_code = fixed_test_code.replace("```java", "").replace("```", "").strip()

        # Append GPT's response to the conversation history
        gpt_msg = {
            "role": "system",
            "content": fixed_test_code
        }
        conversation_history.append(gpt_msg)

        return fixed_test_code
    else:
        return None
    
# Function to handle build attempts
def attempt_build(hadoop_common_path, test_file_path, unit_test_code):
    current_code = unit_test_code

    for attempt in range(1, 6):
        print(f"Attempt {attempt}")

        # Write the current unit test code to the file
        write_test_code_to_file(test_file_path, current_code)

        try:
            result = subprocess.run(["mvn", "clean", "install", "-B", "-DskipTests"], cwd=hadoop_common_path, text=True, capture_output=True, check=True)
            # print(result.stdout)
            print("Build succeeded.")
            return current_code  # Return the code that succeeded
        except subprocess.CalledProcessError as e:
            print("Build failed.")
            error_msgs = re.findall(r"\[ERROR\].*", e.output)
            error_msg = "\n".join(error_msgs)
            # print(error_msg)

            if "BUILD FAILURE" in e.output:
                print("Sending extracted error to GPT...")
                gpt_response = send_to_gpt(error_msg, current_code)
                print("Response received from GPT:", gpt_response)

                suggested_fix = gpt_response.strip()
                if suggested_fix:
                    current_code = suggested_fix  # Update the current code with the suggested fix
                else:
                    print("No fix suggested, or the fix did not resolve the issue.")
                    continue  # Continue to the next iteration

    return None  # No working code found after all attempts


def run_test_cases(hadoop_common_path, test_file_path, test_class, current_code):
    for attempt in range(1, 6):
        print(f"Test Attempt {attempt}")

        # Write the current test code to the file before running the tests
        write_test_code_to_file(current_code, test_file_path)

        test_command = [
            "mvn", "clean", "test", "-Pcoverage",
            f"-Dtest={test_class}", "jacoco:report"
        ]
        try:
            result = subprocess.run(test_command, cwd=hadoop_common_path, text=True, capture_output=True, check=True)
            print(result.stdout)
            print("Test cases ran successfully.")
            return current_code  # Return the current (working) code
        except subprocess.CalledProcessError as e:
            print("Test cases failed.")
            error_msgs = re.findall(r"\[ERROR\].*", e.stderr.decode('utf-8'))  # Decode if subprocess output is in bytes
            error_msg = "\n".join(error_msgs)
            print(error_msg)

            # If tests have failed, send the error message to GPT for a suggested fix
            if "Tests run:" in e.stderr.decode('utf-8'):
                print("Sending extracted error to GPT...")
                gpt_response = send_to_gpt(error_msg, current_code)
                print("Response received from GPT:", gpt_response)

                suggested_fix = gpt_response.strip()
                if suggested_fix:
                    print("Suggested fix:")
                    print(suggested_fix)
                    current_code = suggested_fix  # Update current_code with the suggested fix
                else:
                    print("No fix suggested, or the fix did not resolve the issue.")
                    continue  # Continue to the next iteration
            else:
                print("The failure does not seem to be related to test cases.")
                return None  # Return None to indicate no solution found

    return None  # No working code found after all attempts

# Main function
def main():
    check_input_params()
    parameter_name, method_name = sys.argv[1], sys.argv[2]
    unit_test_code = generate_unit_test(parameter_name, method_name)

    if unit_test_code is None:
        print("No valid unit test code generated.")
        return
    
    # Extracting just the base method name from the full method signature
    base_method_name = re.match(r"[\w]+", method_name).group()

    hadoop_common_path = "/home/nvadde2/hadoop/hadoop-common-project/hadoop-common"
    test_file_name = f"{base_method_name}Test.java"
    test_file_path = os.path.join(hadoop_common_path, "src/test/java/org/apache/hadoop/llmgenerated", test_file_name)

    # Ensure the test file directory exists
    os.makedirs(os.path.dirname(test_file_path), exist_ok=True)

    write_test_code_to_file(test_file_path, unit_test_code)

    # Attempt to build and handle errors
    os.chdir(hadoop_common_path)
    suggested_fix = attempt_build(hadoop_common_path, test_file_path, unit_test_code)

    # Extract the class name for the test
    test_class = f"org.apache.hadoop.llmgenerated.{base_method_name}Test"
    if suggested_fix:
        # Replace unit test code with the suggested fix
        write_test_code_to_file(test_file_path, suggested_fix)
        test_success = run_test_cases(hadoop_common_path, test_file_path, test_class, suggested_fix)
        if not test_success:
            print("Test cases did not run successfully after multiple attempts.")

if __name__ == "__main__":
    main()
