<?php
include_once 'connection.php';
	//error_reporting(E_ALL);
    //ini_set('display_errors', 1);
    Class DownloadPicture { //START CLASS
		 
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
	
        public function download_image($email){
         
            
            try{
                //$file_path = $local_image .$newFileName;//$newFileName.".".$extension;
                            
                $query = "SELECT * FROM  profile_picture WHERE Email = '$email'";
                $result = mysqli_query($this->connection, $query);
                if(mysqli_num_rows($result)>0){ //START OUTER IF
                     while($row = mysqli_fetch_assoc($result)){
                        $file_name = $row['URL']; 
                        $base64 = base64_encode(file_get_contents($file_name));
                        $json['success_get_image'] = $base64;
                         //echo 'got url' . $old_file_name. '  bnew file  '. $newFileName; 
                     }
                }else{
                    $json['error_get_image'] = ' user does not have a picture';
                }
                //if some error occurred 
            }catch(Exception $e){
                    $response['error']=true;
                    $response['message']=$e->getMessage();
                }
            echo json_encode($json);
            mysqli_close($this->connection);    
        }//END FUNCTION
    }//END CLASS
 

    $user = new DownloadPicture();
    if(isset($_POST['email'])){
        	$email = $_POST['email'];
            
		if(!empty($email)){
			$user-> download_image($email);	
		}else{
			$json_error['error_match'] = ' POST value empty ';
			echo json_encode($json_error);
		}
		
	}else{
			$json_error['error_match'] = ' missing POST variables ';
			echo json_encode($json_error);
	}



