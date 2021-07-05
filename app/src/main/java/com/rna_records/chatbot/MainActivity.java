package com.rna_records.chatbot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ListView mListView;
    private FloatingActionButton mButtonSend;
    private EditText mEditTextMessage;
    public Bot bot;
    public static Chat chat;
    private ChatMessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity

            startActivity(new Intent(MainActivity.this, Form.class));

        }


        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();

        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (FloatingActionButton) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);
        final File c = new File(Environment.getExternalStorageDirectory().toString()+"/hari/bots/Hari" + File.separator + "chats.txt");



        try {
            c.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                //bot
                if(message.equals("#TAKEMEHOME"))
                {
                    SharedPreferences prefs = getSharedPreferences("address", MODE_PRIVATE);
                    String name = prefs.getString("address", "No address defined");//"No name defined" is the default value.
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+name);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }else
                {
                String response = chat.multisentenceRespond(mEditTextMessage.getText().toString());


                if(c.exists()){

                    try {
                        System.out.println("In it");
                        FileWriter sf = new FileWriter(c,true);
                        BufferedWriter bf = new BufferedWriter(sf);
                        bf.write("Human: "+message);
                        bf.newLine();
                        bf.write("Bot: "+response);
                        bf.newLine();
                        bf.close();
                        sf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(message);
                mimicOtherMessage(response);
                mEditTextMessage.setText("");
                mListView.setSelection(mAdapter.getCount() - 1);
            }}
        });
        //checking SD card availablility
        boolean a = isSDCARDAvailable();
        //receiving the assets from the app directory
        AssetManager assets = getResources().getAssets();
        File jayDir = new File(Environment.getExternalStorageDirectory().toString() + "/chatbot/bots/ChatBot");
        boolean b = jayDir.mkdirs();
        if (jayDir.exists()) {
            //Reading the file
            try {
                for (String dir : assets.list("ChatBot")) {
                    File subdir = new File(jayDir.getPath() + "/" + dir);
                    boolean subdir_check = subdir.mkdirs();
                    for (String file : assets.list("ChatBot/" + dir)) {
                        File f = new File(jayDir.getPath() + "/" + dir + "/" + file);
                        if (f.exists()) {
                            continue;
                        }
                        InputStream in = null;
                        OutputStream out = null;
                        in = assets.open("ChatBot/" + dir + "/" + file);
                        out = new FileOutputStream(jayDir.getPath() + "/" + dir + "/" + file);
                        //copy file from assets to the mobile's SD card or any secondary memory
                        copyFile(in, out);
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(bundle != null) {
            String name, phone, address1, address2, city, pincode, contact1, contact2;
            name = bundle.getString("name");
            phone = bundle.getString("phone");
            address1 = bundle.getString("address1");
            address2 = bundle.getString("address2");
            city = bundle.getString("city");
            pincode = bundle.getString("pincode");
            contact1 = bundle.getString("contact1");
            contact2 = bundle.getString("contact2");
            SharedPreferences.Editor editor = getSharedPreferences("address", MODE_PRIVATE).edit();
            editor.putString("address", address1);
            editor.apply();

            try {
                File file1 = new File(Environment.getExternalStorageDirectory().toString() + "/chatbot/bots/ChatBot/aiml" + File.separator + "phone.aiml");
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/chatbot/bots/ChatBot/aimlif" + File.separator + "phone.aiml.csv");
                file.createNewFile();
                file1.createNewFile();
                byte[] data1 = {1, 1, 0, 0};
//write the bytes in file
                if (file.exists()) {
                    FileWriter f = new FileWriter(file1);
                    BufferedWriter bw = new BufferedWriter(f);
                    bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    bw.newLine();
                    bw.write("<aiml>");
                    bw.newLine();
                    bw.write("<category><pattern> # my phone number #</pattern>");
                    bw.newLine();
                    bw.write("<template>Your phone number is " + phone + "</template>");
                    bw.newLine();
                    bw.write("</category>");
                    bw.newLine();
                    bw.write("<category><pattern># my name #</pattern>");
                    bw.newLine();
                    bw.write("<template>Your name is "+name+ "</template>");
                    bw.newLine();
                    bw.write("</category>");
                    bw.newLine();
                    bw.write("<category><pattern># my address #</pattern>");
                    bw.newLine();
                    bw.write("<template>You live in "+address1+" "+address2+" "+city+" "+pincode+" please type #TAKEMEHOME if you want me to open your address in maps</template>");
                    bw.newLine();
                    bw.write("</category>");
                    bw.newLine();
                    bw.write("<category><pattern># emergency #</pattern>");
                    bw.newLine();
                    bw.write("<template>Your emergency contacts are "+contact1+" and "+contact2+"</template>");
                    bw.newLine();
                    bw.write("</category>");
                    bw.newLine();
                    bw.write("</aiml>");
                    FileWriter fo = new FileWriter(file);
                    BufferedWriter b1 = new BufferedWriter(fo);
                    b1.write("0,# my phone number #,*,*,Your phone number is " + phone + ",phone.aiml");
                    b1.newLine();
                    b1.write("0,# my name #,*,*,Your name is "+name+",phone.aiml");
                    b1.newLine();
                    b1.write("0,# my address #,*,*,You live in "+address1+" "+address2+" "+city+" "+pincode+" please type #TAKEMEHOME if you want me to open your address in maps,phone.aiml");
                    b1.newLine();
                    b1.write("0,# emergency #,*,*,Your emergency contacts are "+contact1+" and "+contact2+",phone.aiml");
                    b1.newLine();
                    b1.close();
                    fo.close();
                    bw.close();
                    f.close();
                    System.out.println("file created: " + file);
                }
            } catch (Exception e) {


            }
        }

        //get the working directory
        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/hari";
        System.out.println("Working Directory = " + MagicStrings.root_path);
        AIMLProcessor.extension =  new PCAIMLProcessorExtension();
        //Assign the AIML files to bot for processing
        bot = new Bot("ChatBot", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
        String[] args = null;
        mainFunction(args);

    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);

        mimicOtherMessage();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }
    //check SD card availability
    public static boolean isSDCARDAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)? true :false;
    }
    //copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    //Request and response of user and the bot
    public static void mainFunction (String[] args) {
        MagicBooleans.trace_mode = false;
        System.out.println("trace mode = " + MagicBooleans.trace_mode);
        Graphmaster.enableShortCuts = true;
        Timer timer = new Timer();
        String request = "Hello.";
        String response = chat.multisentenceRespond(request);

        System.out.println("Human: "+request);
        System.out.println("Robot: " + response);
    }

}