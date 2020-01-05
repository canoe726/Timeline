<?php
  require "db_require.php";
  $db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

  if( !$db ) {
   die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }

  $class_name = $_POST['classNAME'];
  //$class_name = "자료구조";

  $sql = "SELECT * FROM CLASS_LISTS WHERE class_name = '$class_name'";
  $resource = mysqli_query($db, $sql);
  $result = array();

  while($row = mysqli_fetch_array($resource)) {
    array_push($result,
    array('class_name'=>$row[0],
          'class_time'=>$row[1],
          'class_day'=>$row[2],
          'class_code'=>$row[3],
          'professor'=>$row[4],
          'classroom'=>$row[5],
          'information'=>$row[6],
          'class_schedule'=>$row[7]));
  }

  header('Content-Type: application/json; charset=utf8');
  $json = json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE);
  print_r($json);

  mysqli_close($db);
?>
