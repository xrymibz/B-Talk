package com.scandev;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailAty extends Activity {

    private int ListNum = 0;
    private String data;
    private String LocalHost;
    private List<String> init_countList = new ArrayList<String>();
    private List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();
    private String vba_id = "";
    private String destinationFC = "";
    private String vendorcode = "";
    private String address = "";
    private String phone = "";
    private String remark = "";
    private String currentstatus = "";
    private String weight = "";
    private String cube = "";
    private int pocount = 0;

    private ListView listview;
    private Button saveButton;
    private TextView hintView;
    private TextView vba_idView;
    private TextView destinationFCView;
    private TextView vendorcodeView;
    private TextView addressView;
    private TextView phoneView;

    private String sessionid;
    private S11Application application;

    private static final String TAG = "ASYNC_TASK";
    private DetailTask task5;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_wait);

        application = (S11Application) this.getApplication();
        SharedPreferences sp = getSharedPreferences("AITS_Cookie", MODE_PRIVATE);
        sessionid = sp.getString("cookie", "");
        LocalHost = this.getResources().getString(R.string.Localhost);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String str = bundle.getString("str");

        task5 = new DetailTask();
        task5.execute(str);
    }

    private class DetailTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            Log.i(TAG, "Detail doInBackground() called");
            InputStream inputStream = null;
            String result = "";
            HttpPost httppost = new HttpPost(LocalHost
                    + "asnOperationRecord/mobilelist");
            try {
                HttpClient httppostClienttest = CustomHttpClient.getHttpClient();
                List<NameValuePair> nameValuePairstest = new ArrayList<NameValuePair>(
                        1);
                nameValuePairstest.add(new BasicNameValuePair("id", arg0[0]));
                httppost.setEntity(new UrlEncodedFormEntity(
                        nameValuePairstest, HTTP.UTF_8));
                httppost.setHeader("Cookie", "JSESSIONID=" + sessionid);

                HttpResponse mHttpResponse = httppostClienttest
                        .execute(httppost);
                int statustest = mHttpResponse.getStatusLine().getStatusCode();
                System.out.println("status:" + statustest);
                // �����Ӧ����Ϣʵ��
                HttpEntity mHttpEntity = mHttpResponse.getEntity();
                // ��ȡһ��������
                inputStream = mHttpEntity.getContent();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream));
                result = "";
                String line = "";
                while (null != (line = bufferedReader.readLine())) {
                    result += line;
                }

                httppost.abort();

            } catch (ConnectTimeoutException e1) {
                Toast t = new Toast(DetailAty.this);
                t = Toast.makeText(getApplicationContext(),
                        "����ǰ������״�����ѣ����Ժ�����", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                e1.printStackTrace();
            } catch (SocketTimeoutException e2) {
                Toast t = new Toast(DetailAty.this);
                t = Toast.makeText(getApplicationContext(),
                        "����ǰ������״�����ѣ����Ժ�����", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                e2.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "Detail onPostExecute() called");
            //ȡ�������߳�
            if (task5 != null && task5.getStatus() == AsyncTask.Status.RUNNING) {
                task5 = null; // ���Task�������У�����ȡ����
            }

            setContentView(R.layout.activity_detail);
            hintView = (TextView) findViewById(R.id.hint);
            vba_idView = (TextView) findViewById(R.id.vba_id);
            destinationFCView = (TextView) findViewById(R.id.destinationFC);
            vendorcodeView = (TextView) findViewById(R.id.vendorcode);
            addressView = (TextView) findViewById(R.id.address);
            phoneView = (TextView) findViewById(R.id.phone);
            listview = (ListView) findViewById(R.id.resultlist);
            saveButton = (Button) findViewById(R.id.save);

            if (result.contains("signin")) {

                //System.out.println("����signin");
                Intent toLogin = new Intent();
                toLogin.putExtra("reLogin", true);
                toLogin.setClass(DetailAty.this, MainActivity.class);
                toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toLogin);
            } else if (!result.equals("")) {
                try {
                    JSONObject dataJsonobj = new JSONObject(result);
                    data = dataJsonobj.getString("data");
                    if (!data.equals("[]")) {
                        data = data.substring(2, data.length() - 2);
                    }
                    ListNum = Integer.parseInt(dataJsonobj.getString("recordsFiltered"));

                } catch (JSONException e) {
                    System.out.println("mobilelist���ؽ������JSON��������");
                    e.printStackTrace();
                }

                if (ListNum > 0) {

                    String strlist[] = data.split("\",\"|\\],\\[");
                    //System.out.println("���鳤��Ϊ��"+strlist.length);
                    //��ȡ��ǰ״̬
                    currentstatus = strlist[10];
                    if (currentstatus.equals("����Ŀ�Ŀⷿ") || currentstatus.equals("Canceled")) {
                        hintView.setTextColor(Color.rgb(24, 188, 156));
                        hintView.setText("��ǰ״̬Ϊ " + currentstatus + ",�밴���ذ�ť����");
                        saveButton.setTextSize(16);
                        saveButton.setText("����");
                        //��ִ�з��ز����ļ�����
                        saveButton.setOnClickListener(returnClickListener);
                    } else {
                        //����ת��������״̬������ļ�����
                        saveButton.setOnClickListener(mSendClickListener);
                        //��ȡ����б������
                        myData = getData(strlist, strlist.length);
                        //������������
                        vba_id = strlist[0].substring(1);
                        vba_idView.setText(vba_id);
                        //����Ŀ�Ŀⷿ
                        destinationFC = strlist[3];
                        destinationFCView.setText(destinationFC);
                        //���ù�Ӧ�̱���
                        vendorcode = strlist[6];
                        vendorcodeView.setText(vendorcode);
                        //���ù�Ӧ�̵�ַ
                        address = strlist[15];
                        addressView.setText(address);
                        //���ù�Ӧ����ϵ�绰
                        phone = strlist[16];
                        phoneView.setText(phone);
                        //��ȡ����
                        weight = strlist[13];
                        //��ȡ���
                        cube = strlist[14];

				            /*SimpleAdapter adapter=new SimpleAdapter(this, getData(strlist,strlist.length), 
				            		R.layout.item,new String[]{"bol","po","cartoncount","remark"}, 
									new int[]{R.id.bol,R.id.po,R.id.cartoncount,R.id.remark});*/
                        MyListAdapter adapter = new MyListAdapter();
                        listview.setAdapter(adapter);
                    }

                } else {
                    if (saveButton == null) {

                    } else {
                        saveButton.setVisibility(View.INVISIBLE);
                        hintView.setText("�޲�ѯ���");
                    }
                }
            }
        }

    }

    //�Զ���Adapter,�����ȡEditTextֵ/��̬����po��������
    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return ListNum;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        int count = 0;//���������ۼ�textChangeListener

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub
            System.out.println("����������������position����������������" + arg0);
            System.out.println("��������������convertView������������" + arg1);
            final ViewHolder holder;
            if (arg1 == null) {
                System.out.println("arg1Ϊnull");
                holder = new ViewHolder();
                LayoutInflater inflater = DetailAty.this.getLayoutInflater();
                arg1 = inflater.inflate(R.layout.item, null);
                holder.bolView = (TextView) arg1.findViewById(R.id.bol);
                holder.poView = (TextView) arg1.findViewById(R.id.po);
                holder.cartoncountView = (EditText) arg1.findViewById(R.id.cartoncount);
                //holder.remarkView=(EditText)arg1.findViewById(R.id.remark);

                //Ϊ������������OnTouch������
                holder.cartoncountView.setOnTouchListener(new OnTouchListener() {

                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        // TODO Auto-generated method stub
                        if (event.getAction() == MotionEvent.ACTION_UP) {

                            holder.cartoncountView.getText().clear();
                        }
                        return false;
                    }
                });
				
				/*holder.cartoncountView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						holder.cartoncountView.getText().clear();
					}
				});*/

                //Ϊ������������TextChanged������
                holder.cartoncountView.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable e) {
                        // TODO Auto-generated method stub
                        myData.get(holder.ref).put("cartoncount", e.toString());
                        System.out.println("Item" + holder.ref + "changed to: " + myData.get(holder.ref).put("cartoncount", e.toString()));
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1,
                                                  int arg2, int arg3) {
                        // TODO Auto-generated method stub
                        System.out.println("��beforeTextChanged��" + arg0 + "," + arg1 + "," + arg2 + "," + arg3);
                        count++;
                        System.out.println("listener��������" + count);
                    }

                    @Override
                    public void onTextChanged(CharSequence arg0, int arg1,
                                              int arg2, int arg3) {
                        // TODO Auto-generated method stub
                        System.out.println("��onTextChanged��" + arg0 + "," + arg1 + "," + arg2 + "," + arg3);
                    }
                });

                arg1.setTag(holder);
            } else {

                holder = (ViewHolder) arg1.getTag();
            }

            //�жϽ��㣬���У������
            if (holder.cartoncountView.hasFocus()) {
                holder.cartoncountView.clearFocus();
            }

            holder.ref = arg0;

            //��po���д���ʹ�任��
            String po = myData.get(arg0).get("po").toString();
            po = po.replace(" ", "");
            String[] temp = po.split(",");
            pocount = temp.length;
            pocount += 1;
            holder.poView.setLines(pocount);
            po = po.replace(",", "\n");
            holder.poView.setText(po);

            holder.bolView.setText(myData.get(arg0).get("bol").toString());
            //holder.remarkView.setText(myData.get(arg0).get("remark").toString());
            System.out.println("get����data: " + myData.get(arg0).get("cartoncount").toString());
            holder.cartoncountView.setText(myData.get(arg0).get("cartoncount").toString());

            return arg1;
        }


        public class ViewHolder {
            public TextView bolView;
            public TextView poView;
            public EditText cartoncountView;
            //public EditText remarkView;
            public int ref;
        }

    }

    //����б�����Դ
    private List<Map<String, Object>> getData(String[] slist, int total) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < total; i += 18) {//һ�����ݵĳ�����18

            String bol = new String(slist[i + 1]);
            String cartoncount = new String(slist[i + 4]);
            String po = new String(slist[i + 17].substring(0, slist[i + 17].length() - 1));
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("bol", bol);
            map.put("po", po);
            map.put("cartoncount", cartoncount);
            map.put("remark", remark);
            list.add(map);
            //Ϊ��clicklistener�й��������
            String init_count = new String(slist[i + 12]);
            init_countList.add(init_count);
        }
        return list;
    }

    private OnClickListener mSendClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String param1 = "";
            String count = "";
            int totalcount = 0;
            boolean success = true;
            //�������
            for (int i = 0; i < ListNum; i++) {
                param1 += vba_id + '&';
                param1 += myData.get(i).get("bol");
                //param1+=((TextView)listview.getChildAt(i).findViewById(R.id.bol)).getText();
                param1 += "&";
                count = myData.get(i).get("cartoncount").toString();
                if (count.equals("")) {
                    Toast t = new Toast(DetailAty.this);
                    t = Toast.makeText(DetailAty.this, "����������", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                    success = false;
                    break;
                } else {
                    param1 += count;
                    totalcount += Integer.parseInt(count);
                }
                //count=((EditText)listview.getChildAt(i).findViewById(R.id.cartoncount)).getText().toString();
                //param1+=count;
                param1 += "&";
                param1 += myData.get(i).get("remark");
                //param1+=((EditText)listview.getChildAt(i).findViewById(R.id.remark)).getText();
                param1 += "&";
                param1 += init_countList.get(i);
                param1 += ",";
            }

            if (success) {
                String total = String.valueOf(totalcount);
                Intent intent = new Intent();
                intent.setClass(DetailAty.this, UpdateStatusAty.class);
                Bundle bundle = new Bundle();
                bundle.putString("str", param1);
                bundle.putString("status", currentstatus);
                bundle.putString("weight", weight);
                bundle.putString("cube", cube);
                bundle.putString("totalcount", total);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    };

    private OnClickListener returnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(DetailAty.this, ScanOrManualAty.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };
}
