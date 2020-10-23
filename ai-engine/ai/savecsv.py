import pandas as pd
from pathlib import Path

cars = {'Employee Name': ['Proton'],'Employee Number': [9000]}
df = pd.DataFrame(cars, columns= ['Employee Name', 'Employee Number'])
home = str(Path.home())
df.to_csv('/home/goblin/Desktop/export_dataframe.csv', mode='a', index = False, header=True)

print (df)