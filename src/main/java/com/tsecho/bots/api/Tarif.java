package com.tsecho.bots.api;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Tarif {

    //Для отправки и получения номера телефона
    private String phone = null;
    public String getPhone() {
        return phone;
    }//Геттер
    public void setPhone(String phone) {
        this.phone = phone;
    }//Сеттер

    //Для отправки в PlazmaTelegramBot номера договоров
    private Map<String, String> numbersAgreements = new HashMap<>();
    public Map<String, String> getNumbersAgreements() { return numbersAgreements; }//Геттер
    public void setNumbersAgreements(Map<String, String> numbersAgreements) { this.numbersAgreements = numbersAgreements; }//Сеттер

    //номер одного договора
    //По плану в классе PlazmaTelegramBot вызывается setAgreement
    //И используется в классе xmlRequest getAgreement
    private String agreement = null;
    public String getAgreement() { return agreement; }//Геттер
    public void setAgreement(String agreement) { this.agreement = agreement; }//Сеттер

    private String number = null;
    public String getNumber() { return number; }//Геттер
    public void setNumber(String number) { this.number = number; }//Сеттер

    String error = "";
    String botError = "Телеграмм-бот временно не исправен. Обратитесь в техническую поддержку.";
    String service = "Ваш номер телефона не привязан к услуге. Нажмите на кнопку \"Заказать услугу\".";
    String notService = "Для этого договора услуги не определены.";

    public String getTarif(){
        String request = requestTarif("""
                 <ns1:Login>
                 <login>admin</login> <pass>dcgkmpxv0g</pass> </ns1:Login>
                """);

        if (request.equals(error)){
            return botError;
        }else{
            request = requestTarif(" <ns1:getAccounts> <flt>\n" +
                    " <phone>" + getPhone() + "</phone> </flt>\n" +//Здесь должен быть номер
                    " </ns1:getAccounts>\n");

            if(request.equals(error)){
                return service;
            }else{
                if(tarifFile() == null){
                    return notService;
                }else{
                    //Отслеживаем элементы из Tarif.xml
                    NodeList employeeUid = tarifFile().getDocumentElement().getElementsByTagName("uid");//Указание тега, который будем отслеживать

                    //Проверяем существует ли элемент uid
                    if (employeeUid.getLength() <= 0) {
                        return notService;
                    }else if(employeeUid.getLength() >1){
                        return notService;
                    }else if(employeeUid.getLength() == 1){
                        Node employeeUi = employeeUid.item(0);

                        //Третий запрос: определение количества договоров
                        request = requestTarif(" <ns1:getAgreements> <flt>\n" +
                                " <userid>" + employeeUi.getTextContent() + "</userid> </flt>\n" +
                                " </ns1:getAgreements>\n");

                        if(request.equals(error)){
                            return notService;
                        }else{
                            if(tarifFile() == null){
                                return notService;
                            }else{
                                //Отслеживаем элементы из Tarif.xml
                                NodeList employeeAgrm = tarifFile().getDocumentElement().getElementsByTagName("agrmid");//Указание тега, который будем отслеживать
                                NodeList employeeNumber = tarifFile().getDocumentElement().getElementsByTagName("number");//Указание тега, который будем отслеживать

                                if(employeeAgrm.getLength() <=0 & employeeNumber.getLength() <= 0){
                                    return notService;
                                }else if(employeeAgrm.getLength() == 1 & employeeNumber.getLength() == 1){
                                    NodeList employeeBalance = tarifFile().getDocumentElement().getElementsByTagName("balancetext");//Указание тега, который будем отслеживать
                                    Node agrm = employeeAgrm.item(0);
                                    Node balance = employeeBalance.item(0);
                                    Node number = employeeNumber.item(0);
                                    request = requestTarif("<ns1:getVgroups>\n" +
                                            " <flt>\n" +
                                            " <agrmid>"+agrm.getTextContent()+"</agrmid> <archive>0</archive> <agentid>1</agentid> </flt>\n" +
                                            " </ns1:getVgroups>");

                                    if(request.equals(error)) {
                                        return notService;
                                    }else {
                                        if (tarifFile() == null) {
                                            return notService;
                                        } else {
                                            //Отслеживаем элементы из Tarif.xml
                                            NodeList employeeId = tarifFile().getDocumentElement().getElementsByTagName("vgid");//Указание тега, который будем отслеживать
                                            NodeList employeeTarif = tarifFile().getDocumentElement().getElementsByTagName("tarifdescr");//Указание тега, который будем отслеживать
                                            NodeList employeeBlocked = tarifFile().getDocumentElement().getElementsByTagName("blocked");

                                            if(employeeId.getLength() <= 0 & employeeTarif.getLength() <= 0){
                                                return notService;
                                            }else if(employeeId.getLength() > 0 & employeeTarif.getLength() > 0){
                                                String out = null;
                                                String[] id_ = new String[employeeId.getLength()];
                                                String[] tarif_ = new String[employeeId.getLength()];
                                                for(int i = 0; i < employeeId.getLength(); i++){
                                                    Node id = employeeId.item(i);
                                                    Node tarif = employeeTarif.item(i);
                                                    id_[i] = id.getTextContent();
                                                    tarif_[i] = tarif.getTextContent();
                                                }
                                                for(int i = 0; i < id_.length; i++){
                                                    out += "\n" + "ID услуги: " + id_[i]  +
                                                            "\n" + "Тарифный план: " + tarif_[i];
                                                }
                                                Node blocked = employeeBlocked.item(0);
                                                String status = statusToString(blocked.getTextContent());
                                                return "Договор № "+number.getTextContent() +"\nБаланс счета: " +  balance.getTextContent() + " рублей\n" + "\nСтатус услуги: " + status  + out;
                                            }else{
                                                return notService;
                                            }
                                        }
                                    }
                                }else if(employeeAgrm.getLength() > 1 & employeeNumber.getLength() > 1) {
                                    Map<String, String> numbersLength = new HashMap<>();//Создаю массив, чтобы сложить в него номера договоров
                                    for (int i = 0; i < employeeNumber.getLength(); i++) {
                                        Node number = employeeNumber.item(i);
                                        Node agrm = employeeAgrm.item(i);
                                        numbersLength.putIfAbsent(agrm.getTextContent(), number.getTextContent());
                                    }
                                    setNumbersAgreements(numbersLength);
                                    return ("Выберите номер договора:");
                                }
                            }
                        }
                    }
                }
            }
        }
        return notService;
    }

    //Метод для отправки запросов
    public String requestTarif(String xmlIn) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"urn:api3\"> <SOAP-ENV:Body>\n"
                + xmlIn + " </SOAP-ENV:Body>\n" +
                " </SOAP-ENV:Envelope>";
        StringBuilder resp = new StringBuilder();
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://185.35.128.7:34012").openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.connect();
            try {
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(xml);
                wr.flush();
                wr.close();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()
                ));
                String inputLine ;

                while ((inputLine = in.readLine()) != null) {
                    resp.append(inputLine);
                }
                in.close();

            } catch (IOException e) {
                return error;
            }
            //////Вывод сессии
            con.getHeaderFields();
            con.disconnect();

            //Запись в файл и дальнейшая обработка ответа
            try (FileWriter writer = new FileWriter("Tarif.xml", false))//Создаем файл, так как по-другому я xml парсить не умею
            {
                writer.write(resp.toString());
                writer.flush();
            } catch (IOException ex) {
                return error;
            }
        } catch (IOException e) {
            return error;
        }
        return null;
    }

    //Метод, для работы с web.xml (файл, в котором храниться xml-ответ)
    public Document tarifFile(){
        try {
            //xml парсинг ответа, который получили в качестве ответа на запрос указанный выше
            DocumentBuilderFactory factoryNumber = DocumentBuilderFactory.newInstance();
            DocumentBuilder builderNumber = factoryNumber.newDocumentBuilder();
            return builderNumber.parse("Tarif.xml");//Указание файла, который будем парсить;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return null;
        }
    }

    public String statusToString(String status) {
        return switch (status) { //Текущее состояние блокировки:
            case "0" -> // 0-уч. запись активна,
                    "Активна";
            case "1", "4" -> // 1-заблокирована по балансу, 4-по балансу(активная блокировка),
                    "Заблокирована по балансу";
            case "2" ->  // 2-пользователем,
                    "Отключена пользователем";
            default -> // 3-администратором, 10-уч. запись отключена
                    "Отключена оператором";
        };
    }

    public String getClickTarif(){
        String request = requestTarif("""
                 <ns1:Login>
                 <login>admin</login> <pass>dcgkmpxv0g</pass> </ns1:Login>
                """);

        if (request.equals(error)){
            return botError;
        }else{
            request = requestTarif(" <ns1:getAccounts> <flt>\n" +
                    " <phone>" + getPhone() + "</phone> </flt>\n" +//Здесь должен быть номер
                    " </ns1:getAccounts>\n");

            if(request.equals(error)){
                return service;
            }else{
                if(tarifFile() == null){
                    return service;
                }else{
                    //Отслеживаем элементы из Tarif.xml
                    NodeList employeeUid = tarifFile().getDocumentElement().getElementsByTagName("uid");//Указание тега, который будем отслеживать

                    //Проверяем существует ли элемент uid
                    if (employeeUid.getLength() <= 0) {
                        return notService;
                    }else if(employeeUid.getLength() >1){
                        return notService;
                    }else if(employeeUid.getLength() == 1){
                        Node employeeUi = employeeUid.item(0);

                        //Третий запрос: определение количества договоров
                        request = requestTarif(" <ns1:getAgreements> <flt>\n" +
                                " <userid>" + employeeUi.getTextContent() + "</userid> </flt>\n" +
                                " </ns1:getAgreements>\n");

                        if(request.equals(error)){
                            return notService;
                        }else{
                            if(tarifFile() == null){
                                return notService;
                            }else{
                                request = requestTarif("<ns1:getVgroups>\n" +
                                        " <flt>\n" +
                                        " <agrmid>"+getAgreement()+"</agrmid> <archive>0</archive> <agentid>1</agentid> </flt>\n" +
                                        " </ns1:getVgroups>");

                                if(request.equals(error)) {
                                    return notService;
                                }else{
                                    if(tarifFile() == null){
                                        return notService;
                                    }else{
                                        NodeList employeeId = tarifFile().getDocumentElement().getElementsByTagName("vgid");//Указание тега, который будем отслеживать
                                        NodeList employeeTarif = tarifFile().getDocumentElement().getElementsByTagName("tarifdescr");//Указание тега, который будем отслеживать
                                        NodeList employeeBalance = tarifFile().getDocumentElement().getElementsByTagName("balance");//Указание тега, который будем отслеживать
                                        NodeList employeeBlocked = tarifFile().getDocumentElement().getElementsByTagName("blocked");

                                        if(employeeId.getLength() <= 0 & employeeTarif.getLength() <= 0){
                                            return notService;
                                        }else if(employeeId.getLength() > 0 & employeeTarif.getLength() > 0){
                                            String out = null;
                                            String[] id_ = new String[employeeId.getLength()];
                                            String[] tarif_ = new String[employeeId.getLength()];
                                            String[] blocked = new String[employeeBlocked.getLength()];
                                            Node balance = employeeBalance.item(0);
                                            for(int i = 0; i < employeeId.getLength(); i++){
                                                Node id = employeeId.item(i);
                                                Node tarif = employeeTarif.item(i);
                                                Node block = employeeBlocked.item(i);
                                                id_[i] = id.getTextContent();
                                                tarif_[i] = tarif.getTextContent();
                                                blocked[i] = block.getTextContent();
                                            }
                                            for(int i = 0; i < id_.length; i++){
                                                out += "\n" + "Статус услуги: " + statusToString(blocked[i]) +
                                                        "\n" + "ID услуги: " + id_[i]  +
                                                        "\n" + "Тарифный план: " + tarif_[i] + "\n";
                                            }
                                            return "Договор №: " + getNumber() + "\nБаланс счета: " + balance.getTextContent().substring(0, 5) + " рублей\n" + out;
                                        }else{
                                            return notService;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return notService;
    }
}
