import cv2
import numpy as np
import face_recognition as fr
import urllib.request as ur
import os
'''
Required Llib:

cmake-3.17.3
dlib-19.18.0
face_recognition
numpy 1.18.4
openCV
Pillow
'''
path = 'BasicImages'
images = []
classNames = []
imageList = os.listdir(path)


for i in imageList:
    curImage = cv2.imread(f'BasicImages/{i}')
    images.append(curImage)
    print(os.path.splitext(i)[0])
    classNames.append(os.path.split(i)[0])

def findEncodings(imgList):
    ecodeList = []
    for img in imgList:
        convertedImg = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        encode = fr.face_encodings(img[0])
        ecodeList.append(encode)
    return ecodeList

encodingListKnown = findEncodings(images)
print("Encoding Completed")

imgElon = fr.load_image_file('BasicImages/Elon_Musk.jpg')

imgElon = cv2.cvtColor(imgElon, cv2.COLOR_BGR2RGB)

imgElon_test = fr.load_image_file('BasicImages/Elon_Musk_Test.jpg')
imgElon_test = cv2.cvtColor(imgElon_test, cv2.COLOR_BGR2RGB)

faceLoc = fr.face_locations(imgElon)[0]
encodeElon = fr.face_encodings(imgElon)[0]
cv2.rectangle(imgElon, (faceLoc[3], faceLoc[0]), (faceLoc[1], faceLoc[2]),(225,0,225),2)

faceLoc_test = fr.face_locations(imgElon_test)[0]
encodeElon_test = fr.face_encodings(imgElon_test)[0]
cv2.rectangle(imgElon_test, (faceLoc_test[3], faceLoc_test[0]), (faceLoc_test[1], faceLoc_test[2]),(220,0,200),2)

# Comparing images
result = fr.compare_faces([encodeElon],encodeElon_test)
print(result)
faceDistance = fr.face_distance([encodeElon],encodeElon_test)
 # lower the value is best result
cv2.putText(imgElon_test,f'{result} {round(faceDistance[0],2)}',(50,50),cv2.FONT_HERSHEY_COMPLEX,1,(0,0,255),2)

cv2.imshow('Original', imgElon)
cv2.imshow('Test' , imgElon_test)
cv2.waitKey(0)



