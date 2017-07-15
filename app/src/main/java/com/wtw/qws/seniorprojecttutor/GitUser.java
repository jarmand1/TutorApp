package com.wtw.qws.seniorprojecttutor;

/**
 * Created by Chris on 4/2/2017.
 * This class is used by the GSON library to obtain the JSON values.  The names of the strings are lexically equivalent to
 * their JSON counterparts found in the PHP scripts.
 */

public  class GitUser {

        //MainActivity
        String success_log_in, error_log_in, error_log_in_password, error_user_added, error_student_add;
        String tutor_info_update_success, student_info_update_success; //Successful update
        String student_info_success; //When changing from tutor to student.
        String tutor_info_success; //When changing from student to tutor
        String success_user_added;//When creating an account for the first time stu/tutor
        String student_info_update_error, error_update;
        String student_info_error; //When RAM ID is in use
        String success_account_created;
        String error;
        //SignUp
        String success_information_added;
        String error_sign_up;
        String tutor_info;
        String student_info;
        //Main_Menu
        String success_get_info;
        String error_get_info;
        String email, first_name, last_name, is_tutor, phone_number, ram_id, thirty_minute, sixty_minute;
        //SearchPage
        //requested appt
        String error_requested_appt, insert_error_availability, success_information_added_requested_appt;
        //matches
        String error_match;
        String success_match_found;
        String failure_match_not_found;
        String failure_match;
        String Ram_ID, Tutor_ID, Course_ID, Course_Description, Appointment_Date, Appointment_Begin, Appointment_End, First_Name, Last_Name, Thirty_Minute_Rate, Sixty_Minute_Rate,
                Tutor_Email, Student_Email, Tutor_Appointment_ID, Student_Appointment_ID, Building;
        //view appointments
        String Appointment_ID, error_get_appointments, Total;

        //user_availability
        String failure_availability, app_date, app_begin, app_end, course_id, fatal_error, delete_availability_error, delete_availability_success, availability;
        //tutor profile
        String appointment_info, insert_error;
        //delete appointment
        String error_delete_app,delete_appointment_error,delete_appointment_success;
        //check review
        String error_review_check, success_review_check;
        //add review
        String error_add_review, success_review_added, error_review;
        //get reviews
        String error_get_reviews, Review, Rating;
        //upload image
        String success_file_move, error_file_move;
        //download image
        String success_get_image, error_get_image;
    }


