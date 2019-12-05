package com.fulan.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class BaseController {
    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CustomDateEditor dateEditor = new CustomDateEditor(format, true);
        binder.registerCustomEditor(Date.class, dateEditor);
    }

    public Object getAttribute(String attributeName) {
        return this.getRequest().getAttribute(attributeName);
    }

    public void setAttribute(String attributeName, Object object) {
        this.getRequest().setAttribute(attributeName, object);
    }

    public Object getSession(String attributeName) {
        return this.getRequest().getSession(true).getAttribute(attributeName);
    }

    public void setSession(String attributeName, Object object) {
        this.getRequest().getSession(true).setAttribute(attributeName, object);
    }

    public HttpServletRequest getRequest() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) ra).getRequest();
    }

    public HttpServletResponse getResponse() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) ra).getResponse();
    }

    public HttpSession getSession() {
        return this.getRequest().getSession(true);
    }

    public String getParameter(String paraName) {
        return this.getRequest().getParameter(paraName);
    }

    /**
     * 获取表单格式数据(或url拼接参数)
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Map getParameterMap() {
        return this.getRequest().getParameterMap();
    }

    public String getHeader(String headerName) {
        return this.getRequest().getHeader(headerName);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map getHeaderMap() {
        Enumeration headerNames = this.getRequest().getHeaderNames();
        Map headerMap = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            String headerValue = getRequest().getHeader(headerName);
            headerMap.put(headerName, headerValue);
        }
        return headerMap;
    }

    public String getIpAddress() {
        // String ip = this.getRequest().getRemoteAddr();
        // return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
        HttpServletRequest request = getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * 获取服务器ip地址 * @return
     */
    public String getServerIpAddress() {
        InetAddress address;
        String serverIpAddress = null;
        try {
            // 获取的是本地的IP地址
            address = InetAddress.getLocalHost();
            // PC-20140317PXKX/192.168.0.121
            // 192.168.0.121
            serverIpAddress = address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return serverIpAddress;
    }

    /**
     * 获取json格式数据 * @return
     */
   /* @SuppressWarnings("unchecked")
    public Map<String, Object> getRequestMap() {
        try {
            //默认为json
            InputStream inStream = this.getRequest().getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();
            String buffer = "";
            while (null != (buffer = (in.readLine()))) {
                stringBuffer.append(buffer);
            }
            String reqDoc = stringBuffer.toString();
            if (reqDoc == null || reqDoc.equals("")) {
                return null;
            }
            return JsonUtil.toMap(reqDoc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    /**
     * 允许跨域访问
     */
    public void allowCrossDomainAccess() {
        HttpServletResponse servletResponse = getResponse();
        servletResponse.setHeader("Access-Control-Allow-Origin", "*");
        servletResponse.setHeader("Access-Control-Allow-Methods", "POST,GET");
        servletResponse.setHeader("Access-Control-Allow-Headers:x-requested-with", "content-type");
    }
}
