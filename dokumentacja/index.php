<?php
/**
 * Example Application
 *
 * @package Example-application
 */

session_start();

$_SESSION['buildApi_step'] = 0;

require 'libs/Smarty.class.php';

ini_set('date.timezone', 'Europe/Warsaw');
date_default_timezone_set('Europe/Warsaw');

$smarty = new Smarty;

//$smarty->force_compile = true;
$smarty->debugging = false;
$smarty->caching = false;
$smarty->clear_all_cache();

$smarty->assign(
   "Dashboard", 
   array(
      array("name" => "Dashboard", "section" => "dashboard", "template" => "dashboard.tpl", "data" => null),
      
      array("name" => "Design", "section" => "design", "template" => null, "data" => array(
         array("page" => "adminpanel", "name" => "Admin panel", "template" => "design_adminpanel.tpl")
         )
      ),
      array("name" => "API", "section" => "api", "template" => null, "data" => array(
         array("page" => "backend", "name" => "Backend", "template" => "api.tpl")
         )
      ),     
      array("name" => "Model", "section" => "model", "template" => null, "data" => array(
         array("page" => "database", "name" => "Database", "template" => "model_db.tpl")
         )
      )
   )
);

$smarty->assign(
   "Endpoints", 
   array(
      array("api" => "Sample (Runner)",       "url" => "http://46.41.138.32:9100")
   )      
);

$smarty->assign(
   "Builders", 
   array(
      array("name" => "Build api projects", "action" => "restart(this, 'api')", "img_class" => "apio", "img_src" => "res/api.png"),
      array("name" => "Build front projects", "action" => "restart(this, 'front')", "img_class" => "apio2", "img_src" => "res/bfp.png")
   )      
);

$smarty->assign(
   "Restarters", 
   array(
      array("name" => "Restart all",               "action" => "",             "img_class" => "apio3", "img_src" => "res/rea.png" )
   )      
);


$smarty->display('index.tpl');