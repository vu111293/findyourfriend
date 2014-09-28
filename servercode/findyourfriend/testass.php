<?php

$file_path = 'uploads/images/';

$success = file_put_contents($file_path . "afile", "This is a test");

if($success === false) {
    echo "Couldn't write file";
} else {
    echo "Wrote $success bytes";
}

?>        