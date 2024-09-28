# receipt-processor-challenge
Pull down repo

Simple run instructions:
- Go to root project folder where Dockerfile resides
- Make sure your Docker daemon is running
- Docker build run instructions:
    -  `docker build -t receipt-processor-challenge .`
    -  `docker run --rm -p 8080:8080 receipt-processor-challenge`
 
Sample calls:

curl --location --request POST 'http://localhost:8080/receipts/process' \
--header 'Content-Type: application/json' \
--data-raw '{
  "retailer": "Target",
  "purchaseDate": "2022-01-01",
  "purchaseTime": "13:01",
  "items": [
    {
      "shortDescription": "Mountain Dew 12PK",
      "price": "6.49"
    },{
      "shortDescription": "Emils Cheese Pizza",
      "price": "12.25"
    },{
      "shortDescription": "Knorr Creamy Chicken",
      "price": "1.26"
    },{
      "shortDescription": "Doritos Nacho Cheese",
      "price": "3.35"
    },{
      "shortDescription": "   Klarbrunn 12-PK 12 FL OZ  ",
      "price": "12.00"
    }
  ],
  "total": "35.35"
}'

curl --location --request GET 'http://0.0.0.0:8080/receipts/Target_1727562079_3fe47fce-59dc-486e-baac-08d258bc0023/points'


PS: I would delete the `.idea` folder if you are running project on some other IDE (not IntelliJ)
