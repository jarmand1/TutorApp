<?php
include_once 'connection.php';
	//error_reporting(E_ALL);
    //ini_set('display_errors', 1);
    Class UploadPicture { //START CLASS
		 
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
	
        public function upload_image($email, $name){
            //this is our upload folder 
            //$upload_path = 'uploads/';

            //Getting the server ip 
            //$server_ip = gethostbyname(gethostname());
 
            //creating the upload url 
            //$upload_url = 'http://tutorapplication.a2hosted.com/tutorapplication/profile_pictures/';//.$upload_path; 
            $upload_url = 'tutorapplication/uploaded/';//.$upload_path; 
           // echo $upload_url;
            //response array 
            $response = array(); 
 
 
            if($_SERVER['REQUEST_METHOD']=='POST'){
                //checking the required parameters from the request 
                if(isset($_POST['name']) and isset($_FILES['image']['name'])){
                    //connecting to the database 
                    //$con = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect...');
                    //echo $_FILES['image'];
                    $name_file = $_FILES['image']['name'];
                    $tmp_name = $_FILES['image']['tmp_name'];
                    $local_image = "uploaded/";
                    
 
                    //getting file info from the request 
                    $fileinfo = pathinfo($_FILES['image']['name']);
                   // echo $fileinfo;
                    //getting the file extension 
                    //$extension = $fileinfo['extension'];
 
                    //file url to store in the database edit: and db 
                    $file_url = $upload_url . $email; //. '.' . $extension;
 
                    //file path to upload in the server 
                    //$file_path = $upload_path . getFileName() . '.'. $extension; 
                    
                    //array containing image types.  Pass in the ['image']['type'] and retrieve the actual extension
                    $imagetypes = array(
                    'image/png' => '.png',
                    'image/gif' => '.gif',
                    'image/jpeg' => '.jpg',
                    'image/tif' => '.tif',
                    'image/bmp' => '.bmp');
                    //gets the email minus the .com
                    $length = strlen($email);
                    $newEmail = substr($email, 0, $length-4);
                   
                    //file renaming
                    //$extension = end(explode(".", $tmp_name));
                    $extension =  $imagetypes[$_FILES['image']['type']];
                    $newFileName = $newEmail.$extension;
                   // echo $local_image.$newFileName;

                    //trying to save the file in the directory 
                    try{
                        //saving the file 
                        //move_uploaded_file($_FILES['image']['name'],$upload_url .$image['name']);
                        $file_path = $local_image .$newFileName;//$newFileName.".".$extension;
                            
                        $query = "SELECT * FROM  profile_picture WHERE Email = '$email'";
                        $result = mysqli_query($this->connection, $query);
                        if(mysqli_num_rows($result)>0){ //START OUTER IF
                            while($row = mysqli_fetch_assoc($result)){
                                $old_file_name = $row['URL']; 
                                //echo 'got url' . $old_file_name. '  bnew file  '. $newFileName; 

                            }
                            //update pic loc
                            if (file_exists($old_file_name)){
                                //echo $old_file_name;
                                if(unlink($old_file_name)){
                                    if(move_uploaded_file($tmp_name,$file_path)){
                                        $json['success_file_move'] = ' picture updated ln: 85';
                                        //echo '\r\n bleh'. $file_path;
                                        $query3 = "UPDATE profile_picture SET URL='$file_path', Name='$name_file' WHERE Email='$email'";
                                        $result3 = mysqli_query($this->connection, $query3);
                                        if(mysqli_affected_rows($this->connection)>0){
                                            $json['success_file_move'] = 'sql update success ln: 89';
                                        }else{
                                            $json['error_file_move'] = 'sql update failure ln: 91';
                                        }
                                    }else{
                                        $json['error_file_move'] = ' picture NOT updated ln: 94';
                                    }
                                }else{
                                    $json['error_file_move'] = ' error move failed line 97';  
                                }
                            }else{
                                if(move_uploaded_file($tmp_name,$file_path)){
                                    $json['success_file_move'] = 'file moved ln: 101';
                                }else{
                                    $json['error_file_move'] ='file not moved ln:103';
                                }
                            }
                            //$query2 = "INSERT INTO profile_picture (Email, URL, Name) VALUES ('$email', '$file_path', '$name_file')";
                            //$result2 = mysqli_query($this->connection, $query2);
                            //if(mysqli_affected_rows($this->connection)>0){
                             //   $json['success_file_sql'] = 'sql insert success ln:109';
                                
                            //}else{
                            //    $json['error_file_sql'] = 'sql error ln:115';
                           // }
                        }// END OUTER IF
                        else{
                            if(move_uploaded_file($tmp_name,$file_path)){
                                $json['success_file_move'] = 'file moved ln: 101';
                                $query2 = "INSERT INTO profile_picture (Email, URL, Name) VALUES ('$email', '$file_path', '$name_file')";
                                $result2 = mysqli_query($this->connection, $query2);
                                if(mysqli_affected_rows($this->connection)>0){
                                    $json['success_file_sql'] = 'sql insert success ln:109';
                                }else{
                                    $json['error_file_sql'] = 'sql error ln:115';
                                }
                            }else{
                                    $json['error_file_move'] ='file not moved ln:103';
                            }
                     }
                //if some error occurred 
                   }catch(Exception $e){
                        $response['error']=true;
                        $response['message']=$e->getMessage();
                    } 
                //displaying the response 
               // echo json_encode($response);
                echo json_encode($json);
                //closing the connection 
                mysqli_close($this->connection);
                }else{
                    $response['error']=true;
                    $response['message']='Please choose a file';
                }
            }
        }//END FUNCTION
    }//END CLASS
 

    $user = new UploadPicture();
    if(isset($_POST['email'], $_POST['name'])){
        	$email = $_POST['email'];
            $name = $_POST['name'];
		
		if(!empty($email) && !empty($name) && !empty($_FILES)){
			$user-> upload_image($email, $name);	
		}else{
			$json_error['error_match'] = ' POST value empty ';
			echo json_encode($json_error);
		}
		
	}else{
			$json_error['error_match'] = ' missing POST variables ';
			echo json_encode($json_error);
	}



