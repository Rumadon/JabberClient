package ianto.solutions.jabberclient;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    XMPPTCPConnection connect;
    AbstractXMPPConnection connection;
    String goonfleetAddress = "goonfleet.com";
    int portNumber = 64738;
    String goonfleetUserName = Passwords.USER_NAME;
    String goonfleetPassword = Passwords.PASSWORD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToXMPP();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void connectToXMPP() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder()
                            .setServiceName("goonfleet.com")
                            .setPort(portNumber)
//                            .setUsernameAndPassword(goonfleetUserName, goonfleetPassword)
//                .setCompressionEnabled(false)
//                .setConnectTimeout(10000)
//                .setResource("Smack-client")
//                .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
//                .setCustomSSLContext(sslContext)
//                            .setHost(goonfleetAddress)
                            .setDebuggerEnabled(true);

                    connect = new XMPPTCPConnection(builder.build());
                    connection = connect.connect();
                    connection.login(goonfleetUserName, goonfleetPassword);
                } catch (XMPPException | IOException | SmackException e) {
                    Log.e("Jabber", "Bad Connection", e);
                }
//                if (null != connection && connection.isConnected()) {
//
//                    int i = 7+2;
////                    chatManager.getJoinedRooms();
////                    MultiUserChat codeswarm = MultiUserChat.(connection, "codeswarm", chatManager);
//                }
                if (null != connection && connection.isConnected()) {
                    MultiUserChatManager chatManager = MultiUserChatManager.getInstanceFor(connection);
                    MultiUserChat holeSquadSecureChat = chatManager.getMultiUserChat("holesquadsecure@conference.goonfleet.com");
                    try {
                        holeSquadSecureChat.join("rumadon");
                        holeSquadSecureChat.addMessageListener(new MessageListener() {
                            @Override
                            public void processMessage(Message message) {
                                Log.d("Jabber", message.getBody());
                            }
                        });
                    } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
                        Log.e("Jabber", "Error joining HoleSquad", e);
                    }
                    connection.addAsyncStanzaListener(new StanzaListener() {
                        @Override
                        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                            List<ExtensionElement> extensions = packet.getExtensions();
                        }
                    }, new StanzaFilter() {
                        @Override
                        public boolean accept(Stanza stanza) {
                            if ("directorbot@goonfleet.com/GoBot".equals(stanza.getFrom())) {
                                return true;
                            }
                            return false;
                        }
                    });
                }

                return null;
            }
        }.execute();
    }

//    private SSLContext createSSLContext(Context context) throws KeyStoreException,
//            NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {
//        KeyStore trustStore;
//        InputStream in = null;
//        trustStore = KeyStore.getInstance("BKS");
//
//        in = context.getResources().openRawResource(R.raw.ssl_keystore_prod);
//
//        trustStore.load(in, "<keystore_password>".toCharArray());
//
//        TrustManagerFactory trustManagerFactory = TrustManagerFactory
//                .getInstance(KeyManagerFactory.getDefaultAlgorithm());
//        trustManagerFactory.init(trustStore);
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
//        return sslContext;
//    }
}
