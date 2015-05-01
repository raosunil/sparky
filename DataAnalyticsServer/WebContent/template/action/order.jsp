<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
 <%@ taglib prefix="sj" uri="/struts-jquery-tags" %>

<div id="order">


<style>

td {
font-size: 0.65em;
font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;
font-size: 11px;
}
th {
font-size: 0.85em;
border-top: 2px solid #ddd;
border-right: 2px solid #ddd;
border-left: 2px solid #666;
border-bottom: 2px solid #666;
}
table {
border: 1px dotted #666;
width: 80%;
margin: 20px 0 20px 0;
}
th,td {
margin: 0;
padding: 0;
text-align: left;
vertical-align: top;
background-repeat: no-repeat;
list-style-type: none;
}
thead tr {
background-color: #bbb;
}
tr.odd {
background-color: #fff;
}
tr.even {
background-color: #ddd;
}



</style>


  
 



<%java.text.DateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy"); 
java.util.List sampleList = (java.util.List)request.getAttribute("mydataList");
request.setAttribute( "test", sampleList);


session.setAttribute("mydataList",request.getAttribute("mydataList"));

%>

<h1>What time: <%= df.format(new java.util.Date()) %> </h1>


<s:if test="%{mydataList}">
    <div>
    
    Seen Inside 
    
    <display:table id="table" name="mydataList" pagesize="10" requestURI="/action/order">
    	<display:column property="id" title="ID" />
    	<display:column property="smallParticle" title="smallParticle" />
    </display:table>
    
    </div>
</s:if>
</div>
