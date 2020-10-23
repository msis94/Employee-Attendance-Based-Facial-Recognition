import json

# some JSON:
# x =  '{ "name":"John", "age":30, "city":"New York"}'
# x = "{\"emp_name\": 9, \"status\": \"active\"}"
# parse x:
y = json.loads(x)

# the result is a Python dictionary:
print(y["emp_name"])