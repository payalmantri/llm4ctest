import os
import re
import sys
import openai
import subprocess
import shutil
import csv
import itertools

# Set environment variables
openai.api_key = os.environ["OPENAI_API_KEY"]
# OPENAI_API_KEY = os.environ.get('OPENAI_API_KEY')
# print(OPENAI_API_KEY)
# print(openai.api_key)

# Global variable to keep track of the conversation with GPT-3
conversation_history = []
DEBUG = False

# Function to check input parameters
def check_input_params():
    if len(sys.argv) != 4:
        print("Usage: python3  script.py <parameter_name> <method_name> <classname>")
        sys.exit(1)

def append_to_conversation(role, content):
    global conversation_history
    conversation_history.append({"role": role, "content": content})

    # Trim the conversation history to keep only the last 3 interactions
    if len(conversation_history) > 3:
        conversation_history = conversation_history[-3:]

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
def generate_unit_test(parameter_name, method_name, classname):

    # Define the system message
    # system_msg = "You are a skilled Java developer familiar with the Apache Hadoop project. Hadoop is an open-source framework that allows for the distributed processing of large data sets across clusters of computers using simple programming models. It is designed to scale up from single servers to thousands of machines, each offering local computation and storage. Hadoop's codebase is mainly in Java and adheres to Java development best practices. Your task is to generate unit tests for Java methods in the Hadoop project, ensuring the tests are comprehensive and cover various scenarios, including edge cases. The tests should follow Java coding standards and practices suitable for a large-scale, well-maintained open-source project."
    system_msg = ("As a seasoned Java developer, you're tasked with creating unit tests for the "
              "'hadoop-common' module within the Apache Hadoop project, which can be found at "
              "https://github.com/apache/hadoop. The focus is on Configuration tests, crucial for "
              "ensuring the robustness of the software against configuration changes, a common source "
              "of system failures and service outages. Your unit tests should validate the software code "
              "against various values from the 'core-default.xml' file, aiming to catch any misconfigurations. "
              "Follow Java coding standards and best practices appropriate for this large-scale, "
              "open-source project.")

    # user_msg = f"Generate a unit test for the method {method_name} with the configuration parameter {parameter_name} in Hadoop Common project. Ensure the code is in Java and please provide the code without any explanations or text except code."
    # user_msg = (f"Create a Java unit test named '{classname}Test' for '{method_name}' method in the Hadoop Common project, "
    #         f"especially focusing on the configuration parameter '{parameter_name}'. Include all necessary imports. "
    #         "Test for all possible cases to ensure robust detection of misconfigurations. Provide the test code only, "
    #         "with no additional explanations or text.")

    user_msg = (f"Create a Java unit test named '{classname}Test' for the method '{method_name}' in the Hadoop Common project, "
            f"focusing specifically on Configuration tests. These tests are vital for assessing the software's behavior under "
            f"different configuration values, such as the parameter '{parameter_name}'. Do not explicity set parameter values in test code."
            f"Instead use conf.get to read the values. The goal is to ensure the safety and reliability "
            f"of configuration changes, which are often linked to system failures and service outages. "
            "It is crucial that the response comprises only the Java test code itself, devoid of any explanations, comments, or "
            "supplementary text. Include all necessary imports and ensure the test effectively exercises the code under varied "
            "configurations to detect any potential misconfigurations.")

    # Make sure to append system and user messages to the conversation
    # Append the system message only once, at the start of the conversation
    if not conversation_history:  # If the conversation history is empty, append the system message
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
        # unit_test_code = unit_test_code.replace("```java", "").replace("```", "").strip()
        # This will remove '```' followed by 'Java' (in any case) from the beginning of the string
        unit_test_code = re.sub(r'```java\s*', '', unit_test_code, flags=re.IGNORECASE)

        # This will also remove any standalone triple backticks '```' from anywhere in the string
        unit_test_code = unit_test_code.replace("```", "")

        return unit_test_code
    else:
        return None
     
# Function to send error message to GPT
def send_to_gpt(error_msg, unit_test_code):
    # user_msg = f"The Java unit test {unit_test_code} failed with the following error: {error_msg}. Please suggest a fix to resolve this error. Provide complete test code without any explanations or text except code."
    user_msg = (f"Please analyze the error and suggest a corrected version of the test code."
            f"The response should strictly contain only the revised Java code for the test, without any additional text, comments, or explanations. "
            f"Ensure the code is formatted for immediate compilation and testing."
            f"The Java unit test for {unit_test_code} encountered a failure with this error: {error_msg}. ")

    # Always append the latest user message
    append_to_conversation("user", user_msg)

    # Print conversation history before sending to API
    # print("Sending the following conversation history to GPT-3:")
    # for msg in conversation_history:
    #     print(msg)

    response = openai.ChatCompletion.create(model="gpt-4",
                                            # messages=[
                                            #     {
                                            #         "role": "system", 
                                            #         "content": system_msg
                                            #     },
                                            #     {
                                            #         "role": "user", 
                                            #         "content": user_msg
                                            #     }]
                                            messages=conversation_history
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
            # Attempt to build
            subprocess.run(["mvn", "clean", "install", "-B", "-DskipTests"], cwd=hadoop_common_path, text=True, capture_output=True, check=True)
            print("Build succeeded.")
            return True, current_code  # Build was successful, return current code
        except subprocess.CalledProcessError as e:
            print("Build failed.")
            # Extract error messages
            error_msgs = re.findall(r"\[ERROR\].*?(?=\[ERROR\] -> \[Help 1\]|$)", e.output, re.DOTALL)
            error_msg = "\n".join(error_msgs)
            print(error_msg)
            
            if "BUILD FAILURE" in e.output:
                print("Sending extracted error to GPT...")
                gpt_response = send_to_gpt(error_msg, current_code)
                print("Response received from GPT:", gpt_response)

                if gpt_response:
                    suggested_fix = gpt_response.strip()
                    current_code = suggested_fix  # Update the current code with the suggested fix
                    continue
                else:
                    print("No fix suggested, or the fix did not resolve the issue.")

    print("Build failed and no working code found after multiple attempts.")
    return False, current_code  # Build failed, return the latest code

def inject_values_in_config_file(config_file_path, parameter_name, parameter_value):
    # Create a backup of the configuration file
    backup_file_path = config_file_path + ".bak"
    shutil.copyfile(config_file_path, backup_file_path)
    # Read the configuration file
    with open(config_file_path, 'r') as config_file:
        config_file_contents = config_file.read()
    
    # Replace the parameter value in the configuration file
    new_config_file_contents = re.sub(rf"<name>{parameter_name}</name>\s*<value>.*?</value>", rf"<name>{parameter_name}</name><value>{parameter_value}</value>", config_file_contents, flags=re.DOTALL)

    # Write the updated configuration file
    with open(config_file_path, 'w') as config_file:
        config_file.write(new_config_file_contents)

def restore_config_file(config_file_path):
    # Restore the configuration file from the backup
    backup_file_path = config_file_path + ".bak"
    shutil.copyfile(backup_file_path, config_file_path)

def run_test_cases(hadoop_common_path, test_file_path, test_class, suggested_fix, property, config_file_path, goodValues, badValues):
    current_code = suggested_fix
    print("Running test cases...", test_class)

    # Inject the property value in the configuration file
    inject_values_in_config_file(config_file_path, property, goodValues)


    for attempt in range(1, 6):
        print(f"Test Attempt {attempt}")

        # Write the current test code to the file before running the tests
        if attempt > 1:
            write_test_code_to_file(current_code, test_file_path)

        test_command = [
            "mvn", "clean", "test", "-Pcoverage",
            f"-Dtest={test_class}", "jacoco:report"
        ]
        try:
            result = subprocess.run(test_command, cwd=hadoop_common_path, text=True, capture_output=True, check=True)
            print(result.stdout)
            print("Test cases ran successfully.")
            return True  # Return the current (working) code
        except subprocess.CalledProcessError as e:
            print("Test cases failed.")
            error_msgs = re.findall(r"\[ERROR\].*", e.stderr ) # Decode if subprocess output is in bytes
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
        
        finally:
            # Restore the configuration file from the backup
            restore_config_file(config_file_path)

    return None  # No working code found after all attempts


def get_property_description(config_file_path, parameter_name):
    # Read the configuration file
    with open(config_file_path, 'r') as config_file:
        config_file_contents = config_file.read()
    
    # Extract the whole xml block for the parameter
    xml_block = re.search(rf"<name>{parameter_name}</name>.*?</property>", config_file_contents, flags=re.DOTALL).group()
    print(xml_block);

def read_csv_and_execute(csv_file_path, config_file_path):
    with open(csv_file_path, 'r') as csv_file:
        csv_reader = csv.reader(csv_file)
        next(csv_reader)  # Skip the header row
        # Iterate over each row in the CSV file till end of file or 5 rows, whichever is earlier
        for row in itertools.islice(csv_reader, 5):
            parameter_name, method_name, classname, goodValues, badValues = row[0], row[1], row[2], row[3], row[4]
            parameter_xml = get_property_description(config_file_path, parameter_name)
            execute(parameter_xml, method_name, classname, goodValues, badValues)
          
 
# Actual function to execute the script
def execute(parameter_name, method_name, classname, goodValues, badValues):
    unit_test_code = generate_unit_test(parameter_name, method_name, classname)
    # print("Unit test code generated:", unit_test_code)

    if unit_test_code is None:
        print("No valid unit test code generated.")
        return
    
    # Extracting just the base method name from the full method signature
    # base_method_name = re.match(r"[\w]+", method_name).group()
    base_method_name = classname

    hadoop_common_path = "/Users/payalmantri/Desktop/practice/cs527/hadoop/hadoop-common-project/hadoop-common"
    test_file_name = f"{base_method_name}Test.java"
    test_file_path = os.path.join(hadoop_common_path, "src/test/java/org/apache/hadoop/llmgenerated", test_file_name)
    config_file = "/Users/payalmantri/Desktop/practice/cs527/hadoop/hadoop-common-project/hadoop-common/target/classes/core-default.xml"

    # Ensure the test file directory exists
    os.makedirs(os.path.dirname(test_file_path), exist_ok=True)

    write_test_code_to_file(test_file_path, unit_test_code)

    # Attempt to build and handle errors
    os.chdir(hadoop_common_path)
    build_success, suggested_fix = attempt_build(hadoop_common_path, test_file_path, unit_test_code)

    # Extract the class name for the test
    test_class = f"org.apache.hadoop.llmgenerated.{base_method_name}Test"
    if build_success:
        # Run test cases since the build was successful
        test_success = run_test_cases(hadoop_common_path, test_file_path, test_class, suggested_fix, parameter_name, config_file, goodValues, badValues)
        if not test_success:
            print("Test cases did not run successfully after multiple attempts.")
    else:
        print("Build failed after multiple attempts, skipping test case execution.")

# Main function
def main1():
    check_input_params()

    parameter_name, method_name, classname = sys.argv[1], sys.argv[2], sys.argv[3]
    base_method_name = classname
    hadoop_common_path = "/Users/payalmantri/Desktop/practice/cs527/hadoop/hadoop-common-project/hadoop-common"
    test_file_name = f"{base_method_name}Test.java"
    test_file_path = os.path.join(hadoop_common_path, "src/test/java/org/apache/hadoop/llmgenerated", test_file_name)
    config_file = "/Users/payalmantri/Desktop/practice/cs527/hadoop/hadoop-common-project/hadoop-common/target/classes/core-default.xml"
    execute(parameter_name, method_name, classname)


def main():
    # check_input_params()

    # parameter_name, method_name, classname = sys.argv[1], sys.argv[2], sys.argv[3]
    # base_method_name = classname
    hadoop_common_path = "/Users/payalmantri/Desktop/practice/cs527/hadoop/hadoop-common-project/hadoop-common"
    # test_file_name = f"{base_method_name}Test.java"
    # test_file_path = os.path.join(hadoop_common_path, "src/test/java/org/apache/hadoop/llmgenerated", test_file_name)
    config_file = "/Users/payalmantri/Desktop/practice/cs527/hadoop/hadoop-common-project/hadoop-common/target/classes/core-default.xml"
    csv_file_path = "parameter-configurations.csv"

    # restore_config_file(config_file)


    read_csv_and_execute(csv_file_path, config_file)

if __name__ == "__main__":
    main()
