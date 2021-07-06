import sys

def calculate1(f):

	TJsum = 0
	TSsum = 0
	count = 0

	lines = f.readlines()
	for line in lines:
		numbers = [int(num) for num in line.split(',')]
		# print(numbers)
		if(len(numbers) == 2):
			TSsum += numbers[0]
			TJsum += numbers[1]
			count += 1
	f.close()

	if(count > 0):
		print("Single:")
		print()
		print("TJ Average: ", TJsum/count/1000000.0, "ms")
		print("TS Average: ", TSsum/count/1000000.0, "ms")
		print("Total number of data are: ", count)
		print()
		print()
	else:
		print("File is empty")

def calculate2(f2, f3):

	TJsum = 0
	TSsum = 0
	count = 0

	lines = f2.readlines()
	for line in lines:
		numbers = [int(num) for num in line.split(',')]
		# print(numbers)
		if(len(numbers) == 2):
			TSsum += numbers[0]
			TJsum += numbers[1]
			count += 1
	f2.close()
	
	lines = f3.readlines()
	for line in lines:
		numbers = [int(num) for num in line.split(',')]
		# print(numbers)
		if(len(numbers) == 2):
			TSsum += numbers[0]
			TJsum += numbers[1]
			count += 1
	f3.close()


	if(count > 0):
		print("Scaled:")
		print()
		print("TJ Average: ", TJsum/count/1000000.0, "ms")
		print("TS Average: ", TSsum/count/1000000.0, "ms")
		print("Total number of data are: ", count)
		print()
		print()
	else:
		print("File is empty")

f1 = open("/Users/gaohaoming/Desktop/122B/project/p5/demo/Time_Records_1")
f2 = open("/Users/gaohaoming/Desktop/122B/project/p5/demo/Time_Records_2")
f3 = open("/Users/gaohaoming/Desktop/122B/project/p5/demo/Time_Records_3")
f4 = open("/Users/gaohaoming/Desktop/122B/project/p5/demo/Time_Records_4")
f5 = open("/Users/gaohaoming/Desktop/122B/project/p5/demo/Time_Records_5")

calculate1(f1)
calculate2(f2, f3)
calculate2(f4, f5)



# #empty file only if 'empty' command line argument given
# if(len(sys.argv) == 2):
# 	if(sys.argv[1] == 'empty'):
# 		open("/p5log/searchTimes.txt", 'w').close() #empty file after calculating averages
# 		print("The log file was cleared")