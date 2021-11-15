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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//Класс для обработки кнопки Обещанный платеж
public class xmlRequest {

    //Для отправки в PlazmaTelegramBot номера договоров
    private Map<String, String> numbersAgreements = new HashMap<>();
    public Map<String, String> getNumbersAgreements() { return numbersAgreements; }//Геттер
    public void setNumbersAgreements(Map<String, String> numbersAgreements) { this.numbersAgreements = numbersAgreements; }//Сеттер

    //Для отправки и получения номера телефона
    private String phone = null;
    public String getPhone() {
        return phone;
    }//Геттер
    public void setPhone(String phone) {
        this.phone = phone;
    }//Сеттер

    //Для отправки в PlazmaTelegramBot в качестве уведомления о состоянии запроса
    //К пример: подключено; у вас больше одного договора; у вас нет заключенных договоров
    private String usersMessage = null;
    public String getUsersMessage() { return usersMessage; }//Геттер
    public void setUsersMessage(String user) {
        this.usersMessage = user;
    }//Сеттер

    //номер одного договора
    //По плану в классе PlazmaTelegramBot вызывается setAgreement
    //И используется в классе xmlRequest getAgreement
    private String agreement = null;
    public String getAgreement() { return agreement; }//Геттер
    public void setAgreement(String agreement) { this.agreement = agreement; }//Сеттер

    String error = "";
    String responseError = "Для данного договора услуга \"Обещанный платеж\" не предусмотрена. Обратитесь в техническую поддержку.";
    String responseOk = "Услуга \"Обещанный платеж\" подключена.";
    String botError = "Телеграмм-бот временно неисправен. Обратитесь в техническую поддержку.";
    String legalFace = "Ваш договор оформлен на юридическое лицо. Обратитесь в техническую поддержку.";
    String physicalFace = "Ваш договор не оформлен на физическое лицо. Обратитесь в техническую поддержку.";
    String service = "Ваш номер телефона не привязан к услуге. Нажмите на кнопку \"Заказать услугу\".";

    //Метод для авторизации
    public void xmlLoginAndPass() throws MalformedURLException {
        //первый запрос: авторизация
        String exception = request("""
                 <ns1:Login>
                 <login>admin</login> <pass>dcgkmpxv0g</pass> </ns1:Login>
                """);

        if(!exception.equals(error)) {


            //Второй запрос: определение данных пользователя, а именно type и uid
            //И попутно сохраняем ответ в файле web.xml, чтобы можно было распарсить ответ
            exception = request(" <ns1:getAccounts> <flt>\n" +
                    " <phone>" + getPhone() + "</phone> </flt>\n" +//Здесь должен быть номер
                    " </ns1:getAccounts>\n");

            if(xmlFile() == null){
                setUsersMessage(responseError);
            }else{
                //Отслеживаем элементы из web.xml
                NodeList employeeType = xmlFile().getDocumentElement().getElementsByTagName("type");//Указание тега, который будем отслеживать
                NodeList employeeUid = xmlFile().getDocumentElement().getElementsByTagName("uid");//Указание тега, который будем отслеживать

                if(!exception.equals(error)) {

                    if(employeeType.getLength() > 0 & employeeUid.getLength() > 0) {
                        //Проверяем существуют ли элементы type и uid
                        if (employeeType.getLength() == 1 & employeeUid.getLength() == 1) {//Проверяем существует ли type и uid
                            Node employeeTy = employeeType.item(0);
                            Node employeeUi = employeeUid.item(0);

                            //Проверяем является ли пользователь юр. лицом
                            if (employeeTy.getTextContent().equals("1")) {
                                setUsersMessage(legalFace);
                            } else if (employeeTy.getTextContent().equals("2")) {//Проверяем является ли пользователь физ. лицом

                                //Третий запрос: определение количества договоров
                                request(" <ns1:getAgreements> <flt>\n" +
                                        " <userid>" + employeeUi.getTextContent() + "</userid> </flt>\n" +
                                        " </ns1:getAgreements>\n");

                                if(xmlFile() == null){
                                    setUsersMessage(responseError);
                                }else{
                                    //Отслеживаем элементы из web.xml
                                    NodeList employeeAgrm = xmlFile().getDocumentElement().getElementsByTagName("agrmid");//Указание тега, который будем отслеживать
                                    NodeList employeeNumber = xmlFile().getDocumentElement().getElementsByTagName("number");//Указание тега, который будем отслеживать

                                    if (employeeNumber.getLength() > 0 & employeeAgrm.getLength() > 0) {
                                        Map<String, String> numbersLength = new HashMap<>();//Создаю массив, чтобы сложить в него номера договоров
                                        for (int i = 0; i < employeeNumber.getLength(); i++) {
                                            Node ret = employeeNumber.item(i);
                                            Node agrm = employeeAgrm.item(i);
                                            numbersLength.putIfAbsent(agrm.getTextContent(), ret.getTextContent());
                                        }
                                        setNumbersAgreements(numbersLength);//отправляем номера договоров в сеттер, чтобы вызвать эти значения через геттер в классе PlazmatelegramBot
                                        setUsersMessage("Выберите номер договора.");//Отправляем сообщение на экран пользователя о состоянии запроса
                                    } else if(employeeNumber.getLength() == 0 & employeeAgrm.getLength() == 0){
                                        setUsersMessage("У Вас нет заключенного договора.");
                                    }else{
                                        setUsersMessage(responseError);
                                    }
                                }
                            } else {//Пользователь не является ни юр. лицом, ни физ. лицом
                                setUsersMessage(physicalFace);
                            }
                        } else {
                            setUsersMessage(responseError);
                        }
                    }else {
                        setUsersMessage(service);
                    }
                }else{
                    setUsersMessage(responseError);
                }
            }
        }else{
            setUsersMessage(botError);
        }
    }

    //Метод для отправки запросов
    public String request(String xmlIn) {
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
            try (FileWriter writer = new FileWriter("web.xml", false))//Создаем файл, так как по-другому я xml парсить не умею
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
    public Document xmlFile(){
        try {
            //xml парсинг ответа, который получили в качестве ответа на запрос указанный выше
            DocumentBuilderFactory factoryNumber = DocumentBuilderFactory.newInstance();
            DocumentBuilder builderNumber = factoryNumber.newDocumentBuilder();
            return builderNumber.parse("web.xml");//Указание файла, который будем парсить;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return null;
        }
    }

    //Метод для обработки кнопки-номер договора, так как
    //Номер договора должен прийти в класс xmlRequest раньше,
    //Чем будет вызван четвертый запрос
    public String clickButtonsNumberAgreements() {
        try {
            //Повторно отправляем первый запрос, так как может быть случай,
            //Когда пользователь не нажал кнопку "Обещанный платеж" (тут вопрос авторизации)
            String s = request("""
                     <ns1:Login>
                     <login>admin</login> <pass>dcgkmpxv0g</pass> </ns1:Login>
                    """);

            if (!s.equals(error)) {
                //Тут я решил повторно запросить type, так как возможен такой случай,
                //Что пользователь нажмет на кнопку "Обещанный платеж", ему выведутся на экран
                //Кнопки, с указанными в них номерами договоров, и сообщение сохраниться в чате.
                //После он может провести какого-либо рода махинации в поддержке и сменить договор
                //С физ. лица на юр. лицо, и затем нажать на кнопки, которые сохранились в чате, с указанными в них договорами
                //В результате будет подключена услуга "Обещанный платеж" юр. лицу. Лазейка в системе

                //Второй запрос: определение данных пользователя, а именно type
                s = request(" <ns1:getAccounts> <flt>\n" +
                        " <phone>" + getPhone() + "</phone> </flt>\n" +//Здесь должен быть номер
                        " </ns1:getAccounts>\n");

                if (!s.equals(error)) {
                    if (xmlFile() == null) {
                        return responseError;
                    } else {
                        //Отслеживаем элементы из web.xml
                        NodeList employeeLbapi = xmlFile().getDocumentElement().getElementsByTagName("lbapi:getAccountsResponse");//Указание тега, который будем отслеживать
                        Node Lbapi = employeeLbapi.item(0);

                        if (Lbapi.getTextContent().equals("")) {

                            if (xmlFile() == null) {
                                return responseError;
                            } else {
                                NodeList employeeType = xmlFile().getDocumentElement().getElementsByTagName("type");//Указание тега, который будем отслеживать
                                NodeList employeeUid = xmlFile().getDocumentElement().getElementsByTagName("uid");//Указание тега, который будем отслеживать

                                if (employeeType.getLength() > 0 & employeeUid.getLength() > 0) {
                                    Node type = employeeType.item(0);
                                    if (employeeType.getLength() == 1 & employeeUid.getLength() == 1) {//Проверяем существует ли type и uid
                                        if (type.getTextContent().equals("2")) {
                                            //Повторный третий запрос на тот случай, если пользователь нажал на кнопку номер-договора
                                            //И при этом он сменил номер договора

                                            //Четвертый запрос
                                            s = request("<ns1:getPromisePayments>\n" +
                                                    "<flt>\n" +
                                                    "<agrmid>" + getAgreement() + "</agrmid>\n" +
                                                    "</flt>\n" +
                                                    "</ns1:getPromisePayments>");

                                            if (!s.equals(error)) {

                                                if (xmlFile() == null) {
                                                    return responseError;
                                                } else {
                                                    NodeList employeePromtill = xmlFile().getDocumentElement().getElementsByTagName("promtill");//Указание тега, который будем отслеживать

                                                    if (employeePromtill.getLength() > 0) {//Если у пользователя уже был подключен "обещанный платеж"
                                                        ArrayList<String> list = new ArrayList<>();

                                                        for (int i = 0; i < employeePromtill.getLength(); i++) {
                                                            Node employee = employeePromtill.item(i);
                                                            list.add(employee.getTextContent());
                                                        }
                                                        Date dateNow = new Date();
                                                        String date = list.get(list.size() - 1);
                                                        Date datee = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date);

                                                        if (dateNow.getTime() > datee.getTime()) {//Подключение обещанного платежа в том случае, если истек период пользования

                                                            //Пятый запрос: запрос на подключение услуги "Обещанный платеж"
                                                            s = request(" <ns1:PromisePayment> <agrm>" + getAgreement() + "</agrm>\n" +
                                                                    " <summ>" + 100 + "</summ>\n" +
                                                                    " </ns1:PromisePayment>\n");

                                                            if(!s.equals(error)) {

                                                                if (xmlFile() == null) {
                                                                    return responseError;
                                                                } else {
                                                                    NodeList employeeRet = xmlFile().getDocumentElement().getElementsByTagName("ret");//Указание тега, который будем отслеживать

                                                                    if (employeeRet.getLength() == 1) {
                                                                        Node ret = employeeRet.item(0);
                                                                        if (ret.getTextContent().equals("1")) {
                                                                            return responseOk;
                                                                        } else {
                                                                            return responseError;
                                                                        }
                                                                    } else {
                                                                        return responseError;
                                                                    }
                                                                }
                                                            }else {
                                                                return responseError;
                                                            }
                                                        } else {
                                                            return ("Вы активировали платеж в этом месяце. Вторично активирован быть не может.");
                                                        }
                                                    } else if (employeePromtill.getLength() == 0) {//Если у пользователя не был ни разу подключен "Обещанный платеж"
                                                        //пятый запрос: запрос на подключение услуги "Обещанный платеж"
                                                        s = request(" <ns1:PromisePayment>\n<agrm>" + getAgreement() + "</agrm>\n" +
                                                                " <summ>" + 100 + "</summ>\n" +
                                                                " </ns1:PromisePayment>\n");

                                                        if(!s.equals(error)) {

                                                            if (xmlFile() == null) {
                                                                return responseError;
                                                            } else {
                                                                NodeList employeeRet = xmlFile().getDocumentElement().getElementsByTagName("ret");//Указание тега, который будем отслеживать

                                                                if (employeeRet.getLength() == 1) {
                                                                    Node ret = employeeRet.item(0);
                                                                    if (ret.getTextContent().equals("1")) {
                                                                        return responseOk;
                                                                    } else {
                                                                        return responseError;
                                                                    }
                                                                } else {
                                                                    return responseError;
                                                                }
                                                            }
                                                        }else {
                                                            return responseError;
                                                        }
                                                    } else {
                                                        return responseError;
                                                    }
                                                }
                                            }else {
                                                return botError;
                                            }
                                        } else if (type.getTextContent().equals("1")) {
                                            return (legalFace);
                                        } else {
                                            return (physicalFace);
                                        }
                                    } else {
                                        return responseError;
                                    }
                                } else {
                                    return (service);
                                }
                            }
                        } else if (Lbapi.getTextContent().equals("")) {
                            return (service);
                        } else {
                            return responseError;
                        }
                    }
                } else {
                    return botError;
                }
            } else {
                return botError;
            }
        } catch (ParseException e) {
            return responseError;
        }
    }
}