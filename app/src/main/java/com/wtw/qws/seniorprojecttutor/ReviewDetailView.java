package com.wtw.qws.seniorprojecttutor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ReviewDetailView extends AppCompatActivity {

    String review, name;
    double rating;
    TextView txtTitle, txtReview;
    RatingBar ratRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail_view);

        txtTitle = (TextView) findViewById(R.id.txtTitle_ReviewDetail);
        txtReview = (TextView) findViewById(R.id.txtReviewText_ReviewDetail);
        ratRatingBar = (RatingBar) findViewById(R.id.ratRatingBar_ReviewDetail);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            review = extras.getString("review");
            name = extras.getString("first_name") + " " + extras.getString("last_name");
            rating = Double.parseDouble(extras.getString("rating"));
            txtReview.setText(review);
            txtTitle.setText(name);
            ratRatingBar.setNumStars((int)rating);
        }
    }

}
