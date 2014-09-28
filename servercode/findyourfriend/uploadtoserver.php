                                <?php
  
$file_path = "./uploads/images/";
     
$file_path = $file_path . basename( $_FILES['uploadedfile']['name']);
if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $file_path)) {
        echo "success";
} else{
        echo "fail";
}
?>

                            
                            