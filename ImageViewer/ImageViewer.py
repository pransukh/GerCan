#pip install Pillow
from tkinter import *
from PIL import ImageTk, Image

root = Tk()
root.title("IV")
root.iconbitmap('hitmanicon_9ED_icon.ico')

img_1 = ImageTk.PhotoImage(Image.open('1.png'))
img_2 = ImageTk.PhotoImage(Image.open('2.png'))
img_3 = ImageTk.PhotoImage(Image.open('3.png'))


img_list = [img_1,img_2,img_3]
status = Label(root,text="Image 1 of "+str(len(img_list)),bd=1,relief=SUNKEN, anchor=E)

def next(img_num):
    global img_place
    global button_Next
    global button_Back
    
    img_place.grid_forget()
    img_place = Label(image=img_list[img_num-1])
    button_Next = Button(root, text=">>", command=lambda:next(img_num+1))
    button_Back = Button(root, text="<<", command=lambda:back(img_num-1))
    status = Label(root,text="Image "+ str(img_num) +" of "+str(len(img_list)),bd=1,relief=SUNKEN, anchor=E)
    status.grid(row=2,column=0,columnspan=3,sticky=W+E)
    
    if(img_num == len(img_list)):
        button_Next = Button(root,text=">>", state=DISABLED)
        
    img_place.grid(row=0, column=0, columnspan=3)
    button_Back.grid(row=1, column=0)
    button_Next.grid(row=1, column=2)
    
    
    
def back(img_num):
    global img_place
    global button_Next
    global button_Back
    
    img_place.grid_forget()
    img_place = Label(image=img_list[img_num-1])
    button_Next = Button(root, text=">>", command=lambda:next(img_num+1))
    button_Back = Button(root, text="<<", command=lambda:back(img_num-1))
    status = Label(root,text="Image "+ str(img_num) +" of "+str(len(img_list)),bd=1,relief=SUNKEN, anchor=E)
    status.grid(row=2,column=0,columnspan=3,sticky=W+E)
    
    if(img_num == 1):
        button_Back = Button(root,text="<<", state=DISABLED)
        
    img_place.grid(row=0, column=0, columnspan=3)
    button_Back.grid(row=1, column=0)
    button_Next.grid(row=1, column=2)




img_place = Label(image=img_1)
img_place.grid(row=0,column=0,columnspan=3)


button_Back = Button(root, text="<<", command=lambda:back, state=DISABLED)
button_quit = Button(root, text="Exit", command=root.quit)
button_Next = Button(root, text=">>", command=lambda:next(2))

button_Back.grid(row=1, column=0)
button_quit.grid(row=1, column=1)
button_Next.grid(row=1, column=2,pady=10)
status.grid(row=2,column=0,columnspan=3,sticky=W+E)

root.mainloop()
