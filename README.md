#### Question: Our Node.js backend server manages user accounts and keep tracks of appointments. How should the server manages appointments?

#### Summary

We need to define the status of order first:

        CANCELED(0,"CANCELED"),
        NO_PAY(10,"UNPAID"),
        PAID(20,"PAID"),
        SHIPPED(40,"SHIPPED"),
        ORDER_SUCCESS(50,"ORDER SUCCESS"),
        ORDER_CLOSE(60,"ORDER CLOSE");

And status of response:

        SUCCESS(0,"SUCCESS"),
        ERROR(1,"ERROR"),
        NEED_LOGIN(10,"NEED_LOGIN"),
        ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");
```
For this assignment, I build three modules: user, shipping, order.

To avoid time conflicts, everytime consumer makes an appointment, he needs to choose cutting type 
(Hair cut 45min $55 / Shave 30min $30 / Complete Grooming 1hour $70),  start time, shipping id.

For the cutting type, it is a product id and we can use this id to get product data from mmall_product database.

For the start time, for test purpose, I set the time when the order is created as start time. 
Of course you can modify the code to input any time you want.

For the shipping id, we can use this id to get address info from mmall_shipping database.

Once get all the params, 
first, it will calculate the end time based on product id. 
For example, if you choose hair cut, the end time will be start time add 45 min. 
Second, start time and end time will be put into mmall_order databse.

IMPORTANT:
How can we easily find out which time block is free or taken?


  <select id="checkTime" resultType="int" parameterType="map">
    SELECT  count(1) from mmall_order where (status = 10 OR status = 20 OR status = 40)
    AND (send_time &lt;= #{startTime} and end_time &gt;= #{startTime})
    OR (send_time &lt;= #{endTime} and end_time &gt;= #{endTime})
    OR (send_time &gt;= #{startTime} and end_time &lt;= #{endTime})
  </select>

Everytime a new costumer makes an appointment, his input time will be checked. 
There are two conditions that are illegal:

1. you can find an order, whose status is not (50,"ORDER SUCCESS"), (60,"ORDER CLOSE") or (0,"CANCELED"), 
and the new custumer's start time or end time is between this order's start time and end time.

2. you can find an order, whose status is not (50,"ORDER SUCCESS"), (60,"ORDER CLOSE") or (0,"CANCELED"), 
and the new custumer's start time is before this order's start time and end time is after this order's end time.

Small problem: I assume there is only one barber... 
To solve this problem, I need to build another module to record the status of our barbers, 
but I do not want to make it too complex. 
So one simple solution is: once an order is canceled or finished, the number of available barbers recovers. 
If barber number is zero, do the check above; else just minus 1 and add the appointment into the database;



The following is all POST methods and their JSON returns.

```


#### POST methods
##### order
######1.create order

**/order/create.do**

http://localhost:8080/order/create.do?shippingId=1&productId=1


> request

```
shippingId
productId
(start time) I set the current time as default.
```

> response

success

```
{
    "status": 0,
    "data":{
        "orderNo": 1544867495510,
        "payment": 55,
        "status": 10,
        "statusDesc": "UNPAID",
        "paymentTime": "",
        "startTime": "2018-12-15 01:51:35",
        "endTime": "2018-12-15 02:36:35",
        "closeTime": "",
        "createTime": "",
        "orderItemVoList":[
            {
            "orderNo": 1544867495510,
            "productId": 1,
            "productName": "Hair Cut",
            "currentUnitPrice": null,
            "quantity": null,
            "totalPrice": 55,
            "createTime": ""
            }
        ],
        "shippingId": 1,
        "receiverName": "zihao",
        "shippingVo":{
            "receiverName": "zihao",
            "receiverPhone": "010",
            "receiverMobile": "18688888888",
            "receiverCity": "Seattle",
            "receiverDistrict": "UDistrict",
            "receiverAddress": "UW",
            "receiverZip": "100000",
            "receiverProvince": "WA"
        }
    }
}
```

fail
```
{
    "status": 1,
    "msg": "This time period has been scheduled, please choose another time."
}
```

------

####2.get order products

**/order/detail.do**

http://localhost:8080/order/detail.do?orderNo=1544867495510


> request

```
Order number
```

> response

success

```
{
    "status": 0,
    "data":{
        "orderNo": 1544867495510,
        "payment": 55,
        "status": 10,
        "statusDesc": "UNPAID",
        "paymentTime": "",
        "startTime": "2018-12-15 01:51:35",
        "endTime": "2018-12-15 02:36:35",
        "closeTime": "",
        "createTime": "",
        "orderItemVoList":[
            {
            "orderNo": 1544867495510,
            "productId": 1,
            "productName": "Hair Cut",
            "currentUnitPrice": null,
            "quantity": null,
            "totalPrice": 55,
            "createTime": ""
            }
        ],
        "shippingId": 1,
        "receiverName": "zihao",
        "shippingVo":{
            "receiverName": "zihao",
            "receiverPhone": "010",
            "receiverMobile": "18688888888",
            "receiverCity": "Seattle",
            "receiverDistrict": "UDistrict",
            "receiverAddress": "UW",
            "receiverZip": "100000",
            "receiverProvince": "WA"
        }
    }
}
```

fail
```
{
    "status": 1,
    "msg": "Cannot find the order"
}
```

####3.order List

http://localhost:8080/order/list.do

**/order/list.do**

> request

```
pageSize(default=10)
pageNum(default=1)
```

> response

success

```
{
    "status": 0,
    "data":{
        "pageNum": 1,
        "pageSize": 10,
        "size": 1,
        "orderBy": null,
        "startRow": 1,
        "endRow": 1,
        "total": 1,
        "pages": 1,
        "list":[
            {
                "orderNo": 1544867495510,
                "payment": 55,
                "status": 10,
                "statusDesc": "UNPAID",
                "paymentTime": "",
                "startTime": "2018-12-15 01:51:35",
                "endTime": "2018-12-15 02:36:35",
                "closeTime": "",
                "createTime": "2018-12-15 01:51:35",
                "orderItemVoList":[{"orderNo": 1544867495510, "productId": 1, "productName": "Hair Cut", "currentUnitPrice": null,…],
                "shippingId": 1,
                "receiverName": "zihao",
                "shippingVo":{"receiverName": "zihao", "receiverPhone": "010", "receiverMobile": "18688888888", "receiverCity": "Seattle",…}
            }
        ],
        "firstPage": 1,
        "prePage": 0,
        "nextPage": 0,
        "lastPage": 1,
        "isFirstPage": true,
        "isLastPage": true,
        "hasPreviousPage": false,
        "hasNextPage": false,
        "navigatePages": 8,
        "navigatepageNums":[
            1
        ]
    }
}
```

fail
```
{
  "status": 10,
  "msg": "Need Login"
}


OR

{
  "status": 1,
  "msg": "No permission"
}



```

------

####5.cancel order

http://localhost:8080/order/cancel.do?orderNo=1544867495510

**/order/cancel.do**

> request

```
orderNo
```

> response

success

```
{
  "status": 0
}

```

fail
```
{
  "status": 1,
  "msg": "Cannot find the order"
}

OR
{
  "status": 1,
  "msg": "You have paid the order, cannot cancel"
}
```
[MENU]

####1.log in


**/user/login.do**  post

> request

```
username,password
```
> response

fail
```
{
    "status": 1,
    "msg": "incorrect password"
}
```

success
```
{
    "status": 0,
    "data": {
        "id": 12,
        "username": "aaa",
        "email": "aaa@163.com",
        "phone": null,
        "role": 0,
        "createTime": 1479048325000,
        "updateTime": 1479048325000
    }
}
```


-------

####2.sign up
**/user/register.do**

> request

```
username,password,email,phone,question,answer
```


> response

success
```
{
    "status": 0,
    "msg": "Successfully verified"
}
```


fail
```
{
    "status": 1,
    "msg": "user exists"
}
```


--------

####3.check if username invalid

**/user/check_valid.do**

/check_valid.do?str=admin&type=username



> request

```
str,type
str can be username or email type is username or email

```

>response

success
```
{
    "status": 0,
    "msg": "Successfully verified"
}

```

fail
```
{
    "status": 1,
    "msg": "user exists"
}

```


-----------


####4.get user info
**/user/get_user_info.do**


> request

```
N/A
```
> response

success
```
{
    "status": 0,
    "data": {
        "id": 12,
        "username": "aaa",
        "email": "aaa@163.com",
        "phone": null,
        "role": 0,
        "createTime": 1479048325000,
        "updateTime": 1479048325000
    }
}
```

fail
```
{
    "status": 1,
    "msg": "Need Login"
}

```


------

####5.forget password
**/user/forget_get_question.do**

localhost:8080/user/forget_get_question.do?username=geely



> request

```
username
```
> response

success

```
{
    "status": 0,
    "data": "question"
}
```

fail
```
{
    "status": 1,
    "msg": "Cannot find security question"
}
```


---------

####6.submit answer
**/user/forget_check_answer.do**

localhost:8080/user/forget_check_answer.do?username=aaa&question=aa&answer=sss


> request

```
username,question,answer
```

> response

token



success

```
{
    "status": 0,
    "data": "531ef4b4-9663-4e6d-9a20-fb56367446a5"
}
```

fail

```
{
    "status": 1,
    "msg": "wrong answer"
}
```


------

####7.reset password
**/user/forget_reset_password.do**

localhost:8080/user/forget_reset_password.do?username=aaa&passwordNew=xxx&forgetToken=531ef4b4-9663-4e6d-9a20-fb56367446a5

> request

```
username,passwordNew,forgetToken
```

> response

success

```
{
    "status": 0,
    "msg": "Successfully reset password"
}
```

fail
```
{
    "status": 1,
    "msg": "Cannot reset password"
}
```
OR
```
{
    "status": 1,
    "msg": "token expires"
}
```


------
####8.reset password with Login status
**/user/reset_password.do**

> request

```
passwordOld,passwordNew

```

> response

success

```
{
    "status": 0,
    "msg": "Successfully reset password"
}
```

fail
```
{
    "status": 1,
    "msg": "incorrect old password"
}
```

------
####9.update info
**/user/update_information.do**

> request

```
email,phone,question,answer
```

> response

success

```
{
    "status": 0,
    "msg": "Successfully update info"
}
```

fail
```
{
    "status": 1,
    "msg": "Need Login"
}
```


------
####10.get detail and login
**/user/get_information.do**


> request

```
N/A
```
> response

success
```
{
    "status": 0,
    "data": {
        "id": 1,
        "username": "admin",
        "password": "",
        "email": "admin@163.com",
        "phone": "13800138000",
        "question": "question",
        "answer": "answer",
        "role": 1,
        "createTime": 1478422605000,
        "updateTime": 1491305256000
    }
}
```

fail
```
{
    "status": 10,
    "msg": "Need Login"
}

```


------


####11.log out
**/user/logout.do**

> request

```
N/A
```

> response

success

```
{
    "status": 0,
    "msg": "Log out successfully"
}
```

fail
```
{
    "status": 1,
    "msg": "Something is wrong"
}
```

[MENU]


####1.add address

**/shipping/add.do**

http://localhost:8080/shipping/add.do?userId=1&receiverName=geely&receiverPhone=010&receiverMobile=18688888888&receiverProvince=%E5%8C%97%E4%BA%AC&receiverCity=%E5%8C%97%E4%BA%AC%E5%B8%82&receiverAddress=%E4%B8%AD%E5%85%B3%E6%9D%91&receiverZip=100000

> request

```
userId=1
receiverName=geely
receiverPhone=010
receiverMobile=18688888888
receiverProvince=WA
receiverCity=Seattle
receiverAddress=4717Brooklyn
receiverZip=98105

```

> response

success

```
{
    "status": 0,
    "msg": "Successfully add address",
    "data": {
        "shippingId": 28
    }
}
```

fail
```
{
    "status": 1,
    "msg": "Cannot add address"
}
```


------


####2.delete address

**/shipping/del.do**

> request

```
shippingId
```

> response

success

```
{
    "status": 0,
    "msg": "Successfully delete address"
}
```

fail
```
{
    "status": 1,
    "msg": "Cannot delete address"
}
```


------


####3.update address

**/shipping/update.do**

http://localhost:8080/shipping/update.do?id=5&receiverName=AAA&receiverPhone=010&receiverMobile=18688888888&receiverProvince=%E5%8C%97%E4%BA%AC&receiverCity=%E5%8C%97%E4%BA%AC%E5%B8%82&receiverDistrict=%E6%B5%B7%E6%B7%80%E5%8C%BA&receiverAddress=%E4%B8%AD%E5%85%B3%E6%9D%91&receiverZip=100000

> request

```
id=1
receiverName=geely
receiverPhone=010
receiverMobile=18688888888
receiverProvince=WA
receiverCity=Seattle
receiverAddress=4717Brooklyn
receiverZip=98105
```

> response

success

```
{
    "status": 0,
    "msg": "Successfully update address"
}
```

fail
```
{
    "status": 1,
    "msg": "Cannot update address"
}
```


------


####4.select address

**/shipping/select.do**

> request

```
shippingId
```

> response

success

```
{
    "status": 0,
    "data": {
        "id": 4,
        "userId": 13,
        "receiverName": "geely",
        "receiverPhone": "010",
        "receiverMobile": "18688888888",
        "receiverProvince": "WA",
        "receiverCity": "Seattle",
        "receiverAddress": "4717Brooklyn",
        "receiverZip": "98105",
        "createTime": 1485066385000,
        "updateTime": 1485066385000
    }
}
```

fail
```
{
    "status": 1,
    "msg": "Need Login"
}
```


------


####5.address list

**/shipping/list.do**

http://localhost:8080/shipping/list.do

> request

```
pageNum(default1),pageSize(default10)
```

> response

success

```
{
    "status": 0,
    "data": {
        "pageNum": 1,
        "pageSize": 10,
        "size": 2,
        "orderBy": null,
        "startRow": 1,
        "endRow": 2,
        "total": 2,
        "pages": 1,
        "list": [
            {
                "id": 4,
                "userId": 13,
                "receiverName": "geely",
                "receiverPhone": "010",
                "receiverMobile": "18688888888",
                "receiverProvince": "WA",
                "receiverCity": "Seattle",
                "receiverAddress": 4717Brooklyn",
                "receiverZip": "98105",
                "createTime": 1485066385000,
                "updateTime": 1485066385000
            }
        ],
        "firstPage": 1,
        "prePage": 0,
        "nextPage": 0,
        "lastPage": 1,
        "isFirstPage": true,
        "isLastPage": true,
        "hasPreviousPage": false,
        "hasNextPage": false,
        "navigatePages": 8,
        "navigatepageNums": [
            1
        ]
    }
}
```

fail
```
{
    "status": 1,
    "msg": "Need Login"
}
```
