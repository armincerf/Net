/**
 * Created by alexdavis on 09/04/15.
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;


class Task2 {
    public static Server makeServer () throws NetException {
        Server server = new Server() {

            @Override
            public void start(int port, String password, List<String> forbidden) throws NetException {
            boolean authenticated = false;
                try {
                    ServerSocket welcomeSocket = new ServerSocket(port);
                    while(true){
                        Socket clientSocket = welcomeSocket.accept();
                        for(String s: forbidden){
                            if (s.equals(clientSocket.getRemoteSocketAddress().toString())){
                                System.out.println("forbidden");
                            }
                        }
                        BufferedReader inFromClient =
                                new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        DataOutputStream outToClient =
                                new DataOutputStream(clientSocket.getOutputStream());
                        String clientPassword = inFromClient.readLine();
                        if(clientPassword.equals(password)){
                            outToClient.writeBytes("Authenticated\n");
                            authenticated = true;
                        }
                        else{
                            outToClient.writeBytes("Wrong password\n");
                        }

                    }
                }
                catch (IOException e){
                    throw new NetException(e.getMessage());
                }
            }
        };
        return server;
    }
    public static Client makeClient () throws NetException {


        Client client = new Client() {
            DataOutputStream outToServer;
            BufferedReader inFromServer;
            BufferedReader inFromUser;
            Socket clientSocket;
            @Override
            public void start(String serverAddress, int serverPort) throws NetException {
                try {
                    clientSocket = new Socket(serverAddress, serverPort);
                    outToServer =
                            new DataOutputStream(clientSocket.getOutputStream());
                    inFromServer =
                            new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    inFromUser =
                            new BufferedReader(new InputStreamReader(System.in));

                    System.out.println("Please enter the server password");
                    outToServer.writeBytes(inFromUser.readLine() + "\n");
                    System.out.println("Server response: "+inFromServer.readLine());

                } catch (IOException e) {
                    throw new NetException(e.getMessage());
                }
            }

            @Override
            public Response connect() throws NetException {
                clientSocket.connect();
            }

            @Override
            public Response sendPassword(String pw) throws NetException {
                return null;
            }

            @Override
            public void clientExit() throws NetException {

            }

            @Override
            public Response serverExit() throws NetException {
                return null;
            }

            @Override
            public Response listDirectory() throws NetException {
                return null;
            }

            @Override
            public Response sendFile(String fileName, String fileContent) throws NetException {
                return null;
            }

            @Override
            public Response receiveFile(String fileName) throws NetException {
                return null;
            }

            @Override
            public Response echoByteArray(byte[] ba) throws NetException {
                return null;
            }

            @Override
            public Response echoString(String s) throws NetException {
                return null;
            }
        };
        return client;
    }
}

interface Server {
    public void start ( int port, String password, List<String> forbidden ) throws NetException; }
// The "port" variable holds the port number at which the
// server will run. The variable "forbidden" implements basic
// client-address based access control (firewall). It contains
// a list (possibly empty) of IP addresses and/or
// FQDNs. Servers connecting from an IP address that is in
// this list, or if there is a FQDN in "forbidden" that resolves
// to that IP address, have their connection request refused
// and an appropriate message is returned to the client (see
// below). If the list contains strings that are neither valid
// IP addresses or valid FQDNs, these are simply ignored.
//
// Once the client has connected, the client must supply a
// password (so connecting is different from the password
// exchange).  If the client successfully returns the correct
// password, it gains full access to all the servers
// functionality. Otherwise the server sends a suitable error
// message back to the client. If the client requests server
// functionality without being successfully authenticated, the
// server returns suitable error messages (see below).

interface Response {}
// This is the interface for the various responses that the server
// returns to the client. See below for detailed description.

class OK implements Response {}
class CannotConnect implements Response {}
class ClientBlocked implements Response {}
class AuthenticationFailed implements Response {}
class DirectoryListing implements Response {
    public String [] dir_;
    public DirectoryListing ( String [] dir ) {
        dir_ = new String [ dir.length ];
        System.arraycopy( dir, 0, dir_, 0, dir.length ); } }
class CannotSendFile implements Response {}
class CannotRecieveFile implements Response {}
class FileContent implements Response {
    public String fileData_;
    public FileContent ( String fileData ) { fileData_ = fileData; } }
class ByteArrayContent implements Response {
    public byte [] ba_;
    public ByteArrayContent ( byte [] ba ) {
        ba_ = new byte [ ba.length ];
        System.arraycopy( ba, 0, ba_, 0, ba.length ); } }
class StringContent implements Response {
    public String s_;
    public StringContent ( String s ) { s_ = s; } }
class Problem implements Response {
    // The Problem class can be used by the server to inform the
    // client that something went wrong (other than the cases covered
    // by ClientBlocked, AuthenticationFailed,
    // CannotSendFileCannotRecieveFile).
    public String msg_;
    public Problem ( String msg ) { msg_ = msg; } }


interface Client {

    public void start ( String serverAddress, int serverPort ) throws NetException;
    // Creates a connection with the given server. If you are
    // using TCP as underlying transport prototol (you don't have
    // to, it's up to you), then this would mean starting a client
    // socket.



    public Response connect () throws NetException;
    // Creates a connection to the server (but does not yet send a
    // password). The method returns OK if connection succeeds. If
    // the client is from an IP address that is blocked by the
    // server, ClientBlocked is returned. If the connection
    // attempt is refused for other reasons, CannotConnect is
    // returned. If connect is called before start, then a
    // suitable exception should be thrown.

    public Response sendPassword ( String pw ) throws NetException;
    // Returns OK if password passing is successful, else
    // AuthenticationFailed is returned.  There is no need to do
    // anything sophisticated like refusing further connection
    // attempts or sent passwords for 10 seconds attempts after
    // three failures. If sendPassword is called before connect,
    // then a suitable exception should be thrown.

    public void clientExit () throws NetException;
    // Sends a message to the server indicating the wish to
    // terminate the connection, and exits the client
    // unconditionally (i.e. works even if the client has not
    // successfully been authenticated by the server). The server
    // acknowledges the disconnection message by sending an
    // acknowledgement to the client, and then the server closes
    // the socket. When the client receives the acknowledgement,
    // it terminates. The server itself does NOT terminate.  If
    // clientExit is called before connect, then a suitable
    // exception should be thrown.

    // All methods below require that the client has been password
    // authenticated before. They will always fail with
    // AuthenticationFailed if they had not been already password
    // authenticated. We will not mention the requirement for
    // authentication below, but it is in place.

    public Response serverExit () throws NetException;
    // Sends a message to the server indicating the wish to
    // terminate the connection and also the server.  The server
    // acknowledges the termination request, then closes the
    // connection with the client, and finally terminates itself
    // (as mentioned above, the request will fail if the client
    // has not previously been authenticated).  When the client
    // receives the acknowledgment, it will also terminate.

    public Response listDirectory () throws NetException;
    // Returns an instance of DirectoryListing with the local
    // directory (i.e. a list of file names) stored in dir_. Each
    // array element in dir_ should contain one directory entry.

    public Response sendFile ( String fileName, String fileContent ) throws NetException;
    // Takes a file, here represented by fileContent, sends it to
    // the server, which tries to store it in it's local directory
    // under the name fileName. If this is not possible,
    // e.g. because a file with that name already exists, it
    // returns CannotSendFile to the client, otherwise it returns
    // OK.

    public Response receiveFile ( String fileName ) throws NetException;
    // If the server has a file with the name given in fileName in
    // its local directory, it returns the file content using the
    // FileContent class, with the data being stored in public
    // member fileData_. Otherwise it returns a suitable error
    // message using the CannotRecieveFile class.

    public Response echoByteArray ( byte [] ba ) throws NetException;
    // The server sends simply sends back to the client the
    // byte array ba using the ByteArrayContent class.

    public Response echoString ( String s ) throws NetException;
    // The server sends simply sends back to the client the
    // string ba using the StringContent class.

}
