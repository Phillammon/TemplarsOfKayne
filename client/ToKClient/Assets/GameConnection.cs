using UnityEngine;
using System.Collections;
using System.Net; 
using System.Net.Sockets; 
using System.Text;

public class GameConnection : MonoBehaviour {
        UdpClient sendSocket;
        UdpClient recSocket;
        int listenport = 50050;
        
	// Use this for initialization
	void Start () {
            this.sendSocket = new UdpClient();
            this.recSocket = new UdpClient(listenport);
            this.sendSocket.Connect(PlayerPrefs.GetString("server"), 31338);
            this.recSocket.Client.ReceiveTimeout = 100;
	}
	
	// Update is called once per frame
	void Update () {
            string buttonstring = "f|f|f|f|";
            if (Input.GetMouseButtonDown(0)){
                buttonstring = buttonstring + "t|";
            }
            else {
                buttonstring = buttonstring + "f|";
            }
            if (Input.GetMouseButtonDown(1)){
                buttonstring = buttonstring + "t|";
            }
            else {
                buttonstring = buttonstring + "f|";
            }
            Vector3 mousepos = Input.mousePosition;
            mousepos = Camera.main.ScreenToWorldPoint(mousepos);
            int x = (int) (mousepos.x * 100);
            int y = (int) (mousepos.y * 100);
            buttonstring = buttonstring + x + "|" + y;
            byte[] message = Encoding.UTF8.GetBytes(buttonstring);
            this.sendSocket.Send(message, message.Length);
            byte[] response;
            try{
                IPEndPoint endpoint = new IPEndPoint(IPAddress.Any, listenport);
                response = this.recSocket.Receive(ref endpoint);
                string responsestring = System.Text.Encoding.UTF8.GetString(response);
                Debug.Log(responsestring);
                char[] pipe = {'|'};
                char[] slash = {'/'};
                string[] coords = responsestring.Split(pipe);
                GameObject[] prevplayer = GameObject.FindGameObjectsWithTag("placeholder");
                for(var i = 0 ; i < prevplayer.Length ; i ++)
                {
                    Destroy(prevplayer[i]);
                }
                string[] xyplayer = coords[0].Split(slash);
                float xp = (float) int.Parse(xyplayer[0]);
                float yp = (float) int.Parse(xyplayer[1]);
                xp = xp/100;
                yp = yp/100;
                GameObject player = GameObject.Find("Player");
                player.transform.position = new Vector3(xp, yp, 0);
                GameObject newplayer;
                for(var i = 1; i < coords.Length ; i++)
                {
                    string[] xy1 = coords[i].Split(slash);
                    float x1 = (float) int.Parse(xy1[0]);
                    float y1 = (float) int.Parse(xy1[1]);
                    x1 = x1/100;
                    y1 = y1/100;
                    newplayer = GameObject.Instantiate(Resources.Load("Placeholder")) as GameObject;
                    newplayer.transform.position = new Vector3(x, y, -1);
                }
            }
            catch (SocketException e){
                Debug.Log(e.ToString());
            }
            
        }
}
