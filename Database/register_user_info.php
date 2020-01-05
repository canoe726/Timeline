<?php
  require "db_require.php";
  $db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

  $user_id = $_POST['userID'];
  $user_pw = $_POST['userPW'];
  $user_email = $_POST['userEMAIL'];

  if( !$db ) {
   die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }

  $sql = "INSERT INTO user_lists (user_id, user_pw, user_email)
          VALUES ('$user_id', '$user_pw', '$user_email')";
  $result = mysqli_query($db, $sql);

  if($result == false) {
    echo mysqli_error($db);
  }
  mysqli_close($db);  
?>
