import csv


def writeCSV():
	c = csv.writer(open("1000words.csv", "wb"))
	c.writerow(["Word","Clue","Example"])
	with open("1000 words.txt", 'rb') as f:
		count = 0
		entry = []
		for row in f:
			if count == 3:
				count = 0
				entry = []
			if ord(row[0]) == 226:
				count = 0
				entry = []
			else:
				entry.append(row.strip())
				count += 1
				if count == 3:
					#print entry
					c.writerow([entry[0], entry[1], entry[2]])

if __name__ == "__main__":
    writeCSV()