package com.wtw.qws.seniorprojecttutor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;

/**
 * Created by josianearmand on 5/1/17.
 * Sandbox client account credentials:
 * email:
 * password:
 */

public class Paypal extends Activity
{

    //TextView m_response;
    PayPalConfiguration m_configuration;
    // the id is the link to the paypal account, we have to create an app and get its id
    String m_paypalClientId = "AX0mtSI-GHmawBJ8l7mnr7LwNyPJiPqKIjeA4iC4S2aseSVFIryZGHWTVhUPTzYWMylyoQVSq4VdgKHP";
    Intent m_service;
    int m_paypalRequestCode = 999; // some random number
    Button payButton;
    Context context; //for test message on payment process status
    CharSequence text; //another declaration for toast message


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutor_profile);

        //calls the pay functions, and brings up paypal page
        /*payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay(payButton);
            }
        });
        */

        String paymentAmount = getIntent().getStringExtra("Payment Amount");
        pay(paymentAmount);

        m_configuration = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // sandbox for test, production for real
                .clientId(m_paypalClientId);

        m_service = new Intent(this, PayPalService.class);
        m_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration); // configuration above
        startService(m_service);
    }


    void pay(String paymentAmount)
    {

        //replace BigDecimal(10) with another number or variable. i.e: BigDecimal(student_invoice_amount);
        PayPalPayment payment = new PayPalPayment(new BigDecimal(paymentAmount), "USD", "Test payment with Paypal",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, m_paypalRequestCode);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == m_paypalRequestCode)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                //confirm that the payment worked
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if(confirmation != null)
                {
                    String state = confirmation.getProofOfPayment().getState();

                    if (state.equals("approved")) // if the payment worked, the state equals approved
                    {
                        context = getApplicationContext();
                        text = "Payment Approved";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }


                    //m_response.setText("payment approved");
                }
                    else
                {
                       // m_response.setText("error in the payment");
                    context = getApplicationContext();
                    text = "Error in payment";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
                else
            {
                    //m_response.setText("confirmation is null");
                context = getApplicationContext();
                text = "Confirmation is null";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            }
        }
    }

}
