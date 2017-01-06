package ianto.solutions.jabberclient;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

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
    String goonfleetAddress = "mumble.goonfleet.com";
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
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder()
                .setHost(goonfleetAddress)
                .setPort(portNumber)
                .setUsernameAndPassword(goonfleetUserName, goonfleetPassword)
                .setServiceName("goonfleet.com")
                .setDebuggerEnabled(true)
                .setCompressionEnabled(false)
                .setConnectTimeout(10000)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
                .setHost(goonfleetAddress);
//        try {
//            builder = TLSUtils.acceptAllCertificates(builder);
//        } catch (NoSuchAlgorithmException | KeyManagementException e) {
//            Log.e("Jabber", "TLS all certs error", e);
//        }
        connect = new XMPPTCPConnection(builder.build());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {

                    connection = connect.connect();
                    connection.login();
                } catch (XMPPException | IOException | SmackException e) {
                    Log.e("Jabber", "Bad Connection", e);
                }
                if (null != connection && connection.isConnected()) {
                    connection.addAsyncStanzaListener(new StanzaListener() {
                        @Override
                        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                            List<ExtensionElement> extensions = packet.getExtensions();
                        }
                    }, new StanzaFilter() {
                        @Override
                        public boolean accept(Stanza stanza) {
                            return true;
                        }
                    });
                }

                return null;
            }
        }.execute();
    }
}
