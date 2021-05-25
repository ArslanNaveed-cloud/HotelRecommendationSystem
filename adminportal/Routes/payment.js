const express = require("express");

const app = express();

const router = express.Router();
const { resolve } = require("path");

// This is your real test secret API key.

const stripe = require("stripe")(	
    "sk_test_51HdY2mJ0pWGmfZTV2OjPfgfLpmDsaWiALqmakYMZLM9JZLDkAtxXqCRoh67gkOBozxlkQp6yGm1oLq2tQz2CymNt00G9uImyfa");

app.use(express.static("."));

app.use(express.json());

const calculateOrderAmount = amount => 
{

        var amount  = amount;
  

  return amount;

};

router.post("/onlinepayment", async (req, res) => {
console.log("online payments api called");

  const { items } = req.body;
  amount = (items[0].amount)*100;
  amount = amount+1
      // Create a PaymentIntent with the order amount and currency

  const paymentIntent = await stripe.paymentIntents.create({

    amount: calculateOrderAmount(amount),

    currency: "pkr"

  });

  res.send({

    clientSecret: paymentIntent.client_secret

  });

});
module.exports = router;