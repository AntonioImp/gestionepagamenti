from urllib import urlencode
import webbrowser

def paypal_url():
   params = {  
       'business' : "sb-vyxb84550287@business.example.com",
       'cmd': '_xclick',
       'invoice': '507f1f77bcf86cd799439011',
       'amount': 0.01,
       'item_name' : 'my_order_string_reference',
       'item_number' : 'USER_ID',
       'quantity' : 1,
       'currency_code' : 'EUR',
       'notify_url' : "https://bc6efa8ce4b1.ngrok.io" + "/ipn"
   }
   return "https://www.sandbox.paypal.com/cgi-bin/webscr" + '?' + urlencode(params)
  
print(paypal_url())

webbrowser.open(paypal_url())

#https://webhook.site/1456746b-a911-4e29-8a4f-fabc1cf56886
#https://bc6efa8ce4b1.ngrok.io

# Business IT - sb-vyxb84550287@business.example.com - Rmqe-03^
# Personal IT - sb-e6sf64353498@personal.example.com - n-8l$5U1
# Business US - sb-947mcv3804170@business.example.com - 2K{s%XZv
# Personal US - sb-e6sf64353498@personal.example.com - n-8l$5U1
