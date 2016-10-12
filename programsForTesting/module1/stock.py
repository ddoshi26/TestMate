"""
 This is an inventory program : I am assuming this is the inventory program in ALDI. 
 How many food are in the stock, How many food on the shelf, how many food sold. 
   menu:
    for customer, what food you want to buy, how many, Then we check whether we have the food with enough amount.
    for employee, check the food on the shelf, how many left for each kind of food. If it is less than 10. we need put food from stock to the shelf.
Further implementation:
    we check limit amount on sales.
    we can set up different category for food, like vegetable, fruit, diary product, baking product.
    shopping return
""" 
debug = 0
insertBug= 0
fruOnStock = {'apple': 100, 'banana': 200, 'grape': 300}
fruOnShelf = {'apple': 10, 'banana': 20, 'grape': 30}
sorted(fruOnShelf)  # it is not necessatry. I just like the dictionary alphabetically sorted
sorted(fruOnStock)
def customerPath():
    while(1):
        res = int(input("press 1 to select what fruit you would like to purchase\npress 2 to back to the main menu\n"))
        if res == 1 :
            fruit = raw_input("which fruit you like to purchase?\n")  #Using input() does not work with python 2.7 
            if fruit in fruOnShelf:
                amount = int(input("How many you would like to purchase?\n"))
                if amount <= (fruOnShelf[fruit] + fruOnStock[fruit]):
                    fruOnShelf[fruit] -= amount
                    if fruOnShelf[fruit] < 0 :
                        #we could injert a bug here. If I insert this bug, test3 and test4 would fail.
                        if insertBug == 0 :
                            fruOnStock[fruit] += fruOnShelf[fruit] # automatically refill to the shelf.To be simple, we do not let employee refill here.  
                        
                        fruOnShelf[fruit] = 0
                else:
                    print('we only have {} {}'.format(fruOnStock[fruit]+ fruOnShelf[fruit], fruit))
                    fruOnStock[fruit] = 0
                print("Thank you for shopping.\n") # later we can implement check out
            else:
               print("Sorry, we don't have {}\n".format(fruit))
                # we can implement by ask whehter she buy something else    
        elif res == 2 :
            break   

def employeePath():
    while(1) :
        res = int(input("press 1 to check fruit on shelf\npress 2 to check fruit on stock\npress 3 to add fruit on the shelf\npress 4 to add fruit on stock\npress 5 to return main menu\n"))
    
        if res == 1 :
            print(fruOnShelf)
        elif res == 2 :
            print(fruOnStock)
        elif res == 3 :
            for fru in fruOnShelf:
                if (fruOnShelf[fru] < 20) and (fruOnStock[fru] > 0) :
                    # we set every time refill 20 items to the shelf. To be simple, we did not check whether fruOnStock greater than 20.
                    fruOnStock[fru] -= 20 - fruOnShelf[fru]
                    fruOnShelf[fru] += 20 - fruOnShelf[fru]
            print(fruOnShelf)        
        elif res == 4 :
            # to be simple we do not import new kind of fruit
            for fru in fruOnStock :
                if fruOnStock[fru] < 200 :
                    fruOnStock[fru] = 200                        
            print(fruOnStock)
        elif res == 5 :
            break 

def menu(): 
 
    return input("press 1 if you are a customer. \npress 2 if you are a employee\npress 3 to exit\n")

while (1) :
    status = int(menu()) #Casting to int to work with python 2.7

    if status == 1:
        customerPath()
        if debug == 1:
            print(fruOnShelf)
            print(fruOnStock)
    elif status == 2:
        employeePath()
    elif status == 3 :
        print("Good bye!")
        break
    else: 
        menu()
