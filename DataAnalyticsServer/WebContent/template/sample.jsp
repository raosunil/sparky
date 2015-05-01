<%@ taglib prefix="s" uri="/struts-tags" %>
 <%@ taglib prefix="sj" uri="/struts-jquery-tags" %>
 
 <%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
 <%@ taglib prefix="sj" uri="/struts-jquery-tags" %>

<div id="sample">
<div id="navigation">
     <ul><li><a href="/DataAnalyticsServer/logoutSuccess.jsp">Logout</a></li>
     </ul>
    
    <s:url id="remoteurl2" action="/action/order"/>
    <s:url id="remoteurl3" action="/action/about"/>
    <s:url id="remoteurl4" action="/action/configure"/>
        
    <sj:tabbedpanel id="mytabs" animate="true" collapsible="true" selectedTab="2" spinner="Please wait ...">   
      <sj:tab id="tab1" href="%{remoteurl3}" label="About"/>            
      <sj:tab id="tab3"  href="%{remoteurl2}" label="Results"/> 
      <sj:tab id="tab4" href="%{remoteurl4}" label="Configure"/>        
    </sj:tabbedpanel>    
  </div>


<div id="footer">&copy; 2015  <a href="/DataAnalyticsServer/homepage">Sunil Rao</a></div>
</div>

        	
        	


