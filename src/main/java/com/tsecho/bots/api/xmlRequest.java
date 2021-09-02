package com.tsecho.bots.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;


//Класс для обработки кнопки Обещанный платеж
public class xmlRequest {

    private String phone;

    private String numberAgreements;

    private boolean bool = false;
    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    String[] s = new String[]{};

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private int summ = 100;

    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNumberAgreements() {
        return numberAgreements;
    }

    public void setNumberAgreements(String numberAgreements) {
        this.numberAgreements = numberAgreements;
    }

    public StringBuffer request(String xml, String xmlNumber){
        StringBuffer response = new StringBuffer();

        try{

            URL obj = new URL("http://185.35.128.7:34012");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type","application/soap+xml; charset=utf-8");
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
                System.out.println(con.getInputStream());
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println(con.getHeaderFields());

                System.out.println("response"+xmlNumber+":" + response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            con.disconnect();

            //////Вывод сессии
            Map<String, List<String>> myMap = con.getHeaderFields();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    //Метод для авторизации
    public void xmlLoginAndPass() {
        try{


            //Отправка логина и пароля


            //xml для логина и пароля
            String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?> <SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"urn:api3\"> <SOAP-ENV:Body>\n" +
                    " <ns1:Login>\n" +
                    " <login>admin</login> <pass>dcgkmpxv0g</pass> </ns1:Login>\n" +
                    " </SOAP-ENV:Body>\n" +
                    " </SOAP-ENV:Envelope>";
            System.out.println(request(xml, "1"));
            //Блок отправки логина и пароля


            /*try {
                StringBuffer response = new StringBuffer();
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(xml);
                wr.flush();
                wr.close();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()
                ));
                System.out.println(con.getInputStream());
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println(con.getHeaderFields());

                System.out.println("response:" + response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            con.disconnect();

            //Блок определения сессии

            //////Вывод сессии
            Map<String, List<String>> myMap = con.getHeaderFields();

            //Блок для определения type и uid по номеру

            //xml запрос для определения type и uid по номеру телефона
            String xmlNumber = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"urn:api3\"> <SOAP-ENV:Body>\n" +
                " <ns1:getAccounts> <flt>\n" +
                " <phone>"+ "9626400007" +"</phone> </flt>\n" +//Здесь должен быть номер
                " </ns1:getAccounts> </SOAP-ENV:Body>\n" +
                " </SOAP-ENV:Envelope>";


            con = (HttpURLConnection) new URL("http://185.35.128.7:34012").openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.connect();
            StringBuffer responseNumber = null;
            try {
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                responseNumber = new StringBuffer();
                wr.writeBytes(xmlNumber);


                String inputLineNumber;

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()
                ));
                while ((inputLineNumber = in.readLine()) != null) {
                    responseNumber.append(inputLineNumber);
                }
                System.out.println("response на определение" +
                        " type и id по номеру телефона: " + responseNumber.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(xmlNumber);


            //////Запись в файл запроса по номеру телефона
            try (FileWriter writer = new FileWriter("web.xml", false))//Создаем файл, так как по-другому я xml парсить не умею
            {
                writer.write(responseNumber.toString());
                writer.flush();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            //xml парсинг запроса по номеру телефона
            DocumentBuilderFactory factoryNumber = DocumentBuilderFactory.newInstance();
            DocumentBuilder builderNumber = factoryNumber.newDocumentBuilder();
            Document documentNumber = builderNumber.parse("web.xml");//Указание файла, в который осуществляется запись
            NodeList employeeType = documentNumber.getDocumentElement().getElementsByTagName("type");//Указание тега, который будем отслеживать
            NodeList employeeUid = documentNumber.getDocumentElement().getElementsByTagName("uid");//Указание тега, который будем отслеживать
                if (employeeType.getLength() == 1 & employeeUid.getLength() == 1) {//Проверяем существует ли type и uid
                    Node employeeTy = employeeType.item(0);
                    Node employeeUi = employeeUid.item(0);
                    if (employeeTy.getTextContent().equals("2")) {//Если type==2
                        //Отправка запроса на определение количества договоров
                        HttpURLConnection conAgreements = (HttpURLConnection) obj.openConnection();

                        //xml запрос для определения количества договоров по userid (два договора у userid = 1401)
                        String xmlRet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"urn:api3\"> <SOAP-ENV:Body>\n" +
                                " <ns1:getAgreements> <flt>\n" +
                                " <userid>" + employeeUi.getTextContent() + "</userid> </flt>\n" +
                                " </ns1:getAgreements> </SOAP-ENV:Body>\n" +
                                " </SOAP-ENV:Envelope>";

                        conAgreements.setRequestMethod("POST");
                        conAgreements.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
                        conAgreements.setDoOutput(true);
                        DataOutputStream wrAgreements = new DataOutputStream(conAgreements.getOutputStream());
                        wrAgreements.writeBytes(xmlRet);
                        wrAgreements.flush();
                        wrAgreements.close();
                        BufferedReader inAgreements = new BufferedReader(new InputStreamReader(
                                conAgreements.getInputStream()
                        ));
                        String inputLineAgreements;
                        StringBuffer responseAgreements = new StringBuffer();
                        while ((inputLineAgreements = inAgreements.readLine()) != null) {
                            responseAgreements.append(inputLineAgreements);
                        }
                        inAgreements.close();
                        System.out.println("response на определение количества договоров:" + responseAgreements.toString());

                        //////Запись в файл для определения количества договоров
                        try (FileWriter writer = new FileWriter("web.xml", false))//Создаем файл, так как по-другому я xml парсить не умею
                        {
                            writer.write(responseAgreements.toString());
                            writer.flush();
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                        DocumentBuilderFactory factoryRet = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builderRet = factoryRet.newDocumentBuilder();
                        Document documentret = builderRet.parse("web.xml");//Указание файла, в который осуществляется запись
                        NodeList employeeRet = documentret.getDocumentElement().getElementsByTagName("agrmid");//Указание тега, который будем отслеживать
                        if (employeeRet.getLength() == 1) {//Проверка количеств договоров
                            Node employeeRe = employeeRet.item(0);

                            /////Отпрака запроса на проверку: есть ли подключенный обещанный платеж
                            HttpURLConnection con2 = (HttpURLConnection) obj.openConnection();

                            //xml чтобы вернуть список обещанных платежей
                            String xml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"urn:api3\"> <SOAP-ENV:Body>\n" +
                                    " <ns1:getPromisePayments> <flt>\n" +
                                    " <agrmid>" + employeeRe.getTextContent() + "</agrmid> </flt>\n" +
                                    " </ns1:getPromisePayments> </SOAP-ENV:Body>\n" +
                                    " </SOAP-ENV:Envelope>";

                            con2.setRequestMethod("POST");
                            con2.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
                            con2.setDoOutput(true);
                            DataOutputStream wr2 = new DataOutputStream(con2.getOutputStream());
                            wr2.writeBytes(xml2);
                            wr2.flush();
                            wr2.close();
                            BufferedReader i5 = new BufferedReader(new InputStreamReader(
                                    con2.getInputStream()
                            ));
                            String inputLine2;
                            StringBuffer response2 = new StringBuffer();
                            while ((inputLine2 = i5.readLine()) != null) {
                                response2.append(inputLine2);
                            }
                            i5.close();
                            System.out.println("response на подключение:" + response2.toString());

                            //////Запись в файл
                            try (FileWriter writer = new FileWriter("web.xml", false))//Создаем файл, так как по-другому я xml парсить не умею
                            {
                                writer.write(response2.toString());
                                writer.flush();
                            } catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            }

                            //xml парсинг
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();
                            Document document = builder.parse("web.xml");//Указание файла, в который осуществляется запись
                            NodeList employeeElements = document.getDocumentElement().getElementsByTagName("promtill");//Указание тега, который будем отслеживать
                            Date dateNow = new Date();
                            String date = new String();
                            ArrayList list = new ArrayList();
                            if (employeeElements.getLength() > 0) {
                                for (int i = 0; i < employeeElements.getLength(); i++) {//Вывод всех элементов заключенных в теге
                                    Node employee = employeeElements.item(i);
                                    // Получение атрибутов каждого элемента
                                    String attributes = employee.getNamespaceURI();
                                    list.add(employee.getTextContent());
                                    date = (String) list.get(list.size() - 1);
                                    Date datee = new Date();
                                    datee = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date);
                                    if (dateNow.getTime() > datee.getTime()) {
                                        //Подключение обещанного платежа в том случае, если уже был подключен, но не используется на данный момент
                                        HttpURLConnection con3 = (HttpURLConnection) obj.openConnection();

                                        //xml чтобы подключить обещанный платеж
                                        String xml3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"urn:api3\"> <SOAP-ENV:Body>\n" +
                                                " <ns1:PromisePayment> <agrm>" + employeeRe.getTextContent() + "</agrm>\n" +
                                                " <summ>" + summ + "</summ>\n" +
                                                " </ns1:PromisePayment> </SOAP-ENV:Body>\n" +
                                                " </SOAP-ENV:Envelope>";

                                        con3.setRequestMethod("POST");
                                        con3.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
                                        con3.setDoOutput(true);
                                        DataOutputStream wr3 = new DataOutputStream(con3.getOutputStream());
                                        wr3.writeBytes(xml3);
                                        wr3.flush();
                                        wr3.close();
                                        BufferedReader in3 = new BufferedReader(new InputStreamReader(
                                                con3.getInputStream()
                                        ));
                                        String inputLine3;
                                        StringBuffer response3 = new StringBuffer();

                                        while ((inputLine3 = in3.readLine()) != null) {
                                            response3.append(inputLine3);
                                        }
                                        in3.close();
                                        System.out.println("Результат на подключение: " + response3.toString());


                                        //Запись в файл для поределения занчения тега ret
                                        try (FileWriter writer = new FileWriter("web.xml", false))//Создаем файл, так как по-другому я xml парсить не умею
                                        {
                                            writer.write(response3.toString());
                                            writer.flush();
                                        } catch (IOException ex) {
                                            System.out.println(ex.getMessage());
                                        }
                                        DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
                                        DocumentBuilder builder2 = factory2.newDocumentBuilder();
                                        Document document2 = builder2.parse("web.xml");//Указание файла, в который осуществляется запись
                                        NodeList employeeElements2 = document2.getDocumentElement().getElementsByTagName("ret");
                                        for (int i2 = 0; i2 < 1; i2++) {//Вывод всех элементов заключенных в теге
                                            Node employeeResult = employeeElements2.item(i);
                                            if (employeeResult.getTextContent() == "1") {
                                                setUser("Обещанный платеж подключен.");
                                            }
                                        }
                                    }
                                }
                            } else {

                                //Подключение обещанного платежа в том случае, если не подключался никогда

                                System.out.println("Подключено1");
                                HttpURLConnection con3 = (HttpURLConnection) obj.openConnection();

                                //xml чтобы подключить обещанный платеж
                                String xml3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"urn:api3\"> <SOAP-ENV:Body>\n" +
                                        " <ns1:PromisePayment> <agrm>" + employeeRe.getTextContent() + "</agrm>\n" +
                                        " <summ>" + summ + "</summ>\n" +
                                        " </ns1:PromisePayment> </SOAP-ENV:Body>\n" +
                                        " </SOAP-ENV:Envelope>";

                                con3.setRequestMethod("POST");
                                con3.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
                                con3.setDoOutput(true);
                                DataOutputStream wr3 = new DataOutputStream(con3.getOutputStream());
                                wr3.writeBytes(xml3);
                                wr3.flush();
                                wr3.close();
                                BufferedReader in3 = new BufferedReader(new InputStreamReader(
                                        con3.getInputStream()
                                ));
                                String inputLine3;
                                StringBuffer response3 = new StringBuffer();

                                while ((inputLine3 = in3.readLine()) != null) {
                                    response3.append(inputLine3);
                                }
                                in3.close();
                                System.out.println("Результат на подключение: " + response3.toString());

                                //Запись в файл
                                try (FileWriter writer = new FileWriter("web.xml", false))//Создаем файл, так как по-другому я xml парсить не умею
                                {
                                    writer.write(response3.toString());
                                    writer.flush();
                                } catch (IOException ex) {
                                    System.out.println(ex.getMessage());
                                }

                                //xml парсинг
                                DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
                                DocumentBuilder builder2 = factory2.newDocumentBuilder();
                                Document document2 = builder2.parse("web.xml");//Указание файла, в который осуществляется запись
                                NodeList employeeElements2 = document2.getDocumentElement().getElementsByTagName("ret");
                                for (int i = 0; i < 1; i++) {//Вывод всех элементов заключенных в теге
                                    Node employee = employeeElements2.item(i);
                                    if (employee.getTextContent() == "1") {
                                        setUser("Обещанный платеж подключен.");
                                    }
                                }
                            }
                        } else if (employeeRet.getLength() > 1) {
                            PlasmaTelegramBot plazma = new PlasmaTelegramBot();
                            Node ret;
                            String[] s = new String[employeeRet.getLength()];
                            String Agreements = "У Вас больше одного договора. Введите номер договора, для которого Вы хотите подключить услугу \"Обещанный платеж\".\n" +
                                    "Номера договоров:";
                            for(int i=0; i<employeeRet.getLength(); i++){
                                ret = employeeRet.item(i);
                                Agreements += "\n"+ret.getTextContent();
                                s[i] = ret.getTextContent();
                            }
                            setS(s);
                            setUser(Agreements);
                            /////////////////////////////////////////////////////////////////////////////
                            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

                            //xml чтобы вернуть список обещанных платежей
                            String xmlcon = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"urn:api3\"> <SOAP-ENV:Body>\n" +
                                    " <ns1:getPromisePayments> <flt>\n" +
                                    " <agrmid>" + getNumberAgreements() + "</agrmid> </flt>\n" +
                                    " </ns1:getPromisePayments> </SOAP-ENV:Body>\n" +
                                    " </SOAP-ENV:Envelope>";

                            System.out.println(xmlcon);
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
                            connection.setDoOutput(true);
                            DataOutputStream wrcon = new DataOutputStream(connection.getOutputStream());
                            wrcon.writeBytes(xmlcon);
                            wrcon.flush();
                            wrcon.close();
                            BufferedReader incon = new BufferedReader(new InputStreamReader(
                                    connection.getInputStream()
                            ));
                            String inputLinecon;
                            StringBuffer responsecon = new StringBuffer();
                            while ((inputLinecon = incon.readLine()) != null) {
                                responsecon.append(inputLinecon);
                            }
                            incon.close();
                            System.out.println("Запрос который отправил" + xmlcon);
                            System.out.println("response на подключение:" + responsecon.toString());

                            //////Запись в файл
                            try (FileWriter writer = new FileWriter("web.xml", false))//Создаем файл, так как по-другому я xml парсить не умею
                            {
                                writer.write(responsecon.toString());
                                writer.flush();
                            } catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            }

                            //xml парсинг
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();
                            Document document = builder.parse("web.xml");//Указание файла, в который осуществляется запись
                            NodeList employeeElements = document.getDocumentElement().getElementsByTagName("promtill");//Указание тега, который будем отслеживать
                            Date dateNow = new Date();
                            String date = new String();
                            ArrayList list = new ArrayList();
                            if (employeeElements.getLength() > 0) {
                                for (int i1 = 0; i1 < employeeElements.getLength(); i1++) {//Вывод всех элементов заключенных в теге
                                    Node employee = employeeElements.item(i1);
                                    // Получение атрибутов каждого элемента
                                    String attributes = employee.getNamespaceURI();
                                    list.add(employee.getTextContent());
                                    date = (String) list.get(list.size() - 1);
                                    Date datee = new Date();
                                    datee = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date);
                                    if (dateNow.getTime() > datee.getTime()) {
                                        //Подключение обещанного платежа в том случае, если уже был подключен, но не используется на данный момент
                                        HttpURLConnection con3 = (HttpURLConnection) obj.openConnection();

                                        //xml чтобы подключить обещанный платеж
                                        String xml3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"urn:api3\"> <SOAP-ENV:Body>\n" +
                                                " <ns1:PromisePayment> <agrm>" + getNumberAgreements() + "</agrm>\n" +
                                                " <summ>" + 100 + "</summ>\n" +
                                                " </ns1:PromisePayment> </SOAP-ENV:Body>\n" +
                                                " </SOAP-ENV:Envelope>";

                                        con3.setRequestMethod("POST");
                                        con3.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
                                        con3.setDoOutput(true);
                                        DataOutputStream wr3 = new DataOutputStream(con3.getOutputStream());
                                        wr3.writeBytes(xml3);
                                        wr3.flush();
                                        wr3.close();
                                        BufferedReader in3 = new BufferedReader(new InputStreamReader(
                                                con3.getInputStream()
                                        ));
                                        String inputLine3;
                                        StringBuffer response3 = new StringBuffer();

                                        while ((inputLine3 = in3.readLine()) != null) {
                                            response3.append(inputLine3);
                                        }
                                        in3.close();
                                        System.out.println("Результат на подключение: " + response3.toString());


                                        //Запись в файл для поределения занчения тега ret
                                        try (FileWriter writer = new FileWriter("web.xml", false))//Создаем файл, так как по-другому я xml парсить не умею
                                        {
                                            writer.write(response3.toString());
                                            writer.flush();
                                        } catch (IOException ex) {
                                            System.out.println(ex.getMessage());
                                        }
                                        DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
                                        DocumentBuilder builder2 = factory2.newDocumentBuilder();
                                        Document document2 = builder2.parse("web.xml");//Указание файла, в который осуществляется запись
                                        NodeList employeeElements2 = document2.getDocumentElement().getElementsByTagName("ret");
                                        for (int i2 = 0; i2 < 1; i2++) {//Вывод всех элементов заключенных в теге
                                            Node employeeResult = employeeElements2.item(i2);
                                            if (employeeResult.getTextContent() == "1") {
                                                setUser("Обещанный платеж подключен.");
                                            }
                                        }
                                    } else {
                                        setUser("Вы уже подключали в этом месяце услугу.");
                                    }
                                }

                            } else {

                                //Подключение обещанного платежа в том случае, если не подключался никогда

                                System.out.println("Подключено2");
                                HttpURLConnection http = (HttpURLConnection) obj.openConnection();

                                //xml чтобы подключить обещанный платеж
                                String xmlhttp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"urn:api3\"> <SOAP-ENV:Body>\n" +
                                        " <ns1:PromisePayment> <agrm>" + getNumberAgreements() + "</agrm>\n" +
                                        " <summ>" + 100 + "</summ>\n" +
                                        " </ns1:PromisePayment> </SOAP-ENV:Body>\n" +
                                        " </SOAP-ENV:Envelope>";

                                http.setRequestMethod("POST");
                                http.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
                                http.setDoOutput(true);
                                DataOutputStream wrhttp = new DataOutputStream(http.getOutputStream());
                                wrhttp.writeBytes(xmlhttp);
                                wrhttp.flush();
                                wrhttp.close();
                                BufferedReader inhttp = new BufferedReader(new InputStreamReader(
                                        http.getInputStream()
                                ));
                                String inputLinehttp;
                                StringBuffer responsehttp = new StringBuffer();

                                while ((inputLinehttp = inhttp.readLine()) != null) {
                                    responsehttp.append(inputLinehttp);
                                }
                                inhttp.close();
                                System.out.println("Результат на подключение: " + responsehttp.toString());

                                //Запись в файл
                                try (FileWriter writer = new FileWriter("web.xml", false))//Создаем файл, так как по-другому я xml парсить не умею
                                {
                                    writer.write(responsehttp.toString());
                                    writer.flush();
                                } catch (IOException ex) {
                                    System.out.println(ex.getMessage());
                                }

                                //xml парсинг
                                DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
                                DocumentBuilder builder2 = factory2.newDocumentBuilder();
                                Document document2 = builder2.parse("web.xml");//Указание файла, в который осуществляется запись
                                NodeList employeeElements2 = document2.getDocumentElement().getElementsByTagName("ret");
                                for (int i2 = 0; i2 < 1; i2++) {//Вывод всех элементов заключенных в теге
                                    Node employee = employeeElements2.item(i2);
                                    if (employee.getTextContent() == "1") {
                                        setUser("Обещанный платеж подключен.");
                                    }
                                }
                            }
                            /////////////////////////////////////////////////////////////////////////////
                        } else if (employeeRet.getLength() < 1) {
                            setUser("У Вас не заключен ни один договор.");
                        }
                    } else {//Если type==1
                        setUser("Ваш договор оформлен на юридическое лицо. Обратитесь в поддержку.");
                    }
                }else{
                    setUser("Услуг на данный номер телефона не существует.");
                }

            //Исключения
        } catch (IOException | ParserConfigurationException | SAXException | ParseException e) {
            e.printStackTrace();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getS() {

        return s;
    }

    public void setS(String[] s) {
        this.s = s;
    }
}
