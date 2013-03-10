/* author:zs.lin;versions:20111218;date:2011/11/8 */
;$(function(){
	//top navigation
	$('#topnavWrap').mouseover(function(){
		$(this).css('overflow','visible');
		$('#topNav').attr('class','topnav_hover');
	}).mouseout(function(){
		$(this).css('overflow','hidden');
		$('#topNav').attr('class','');
	});
});