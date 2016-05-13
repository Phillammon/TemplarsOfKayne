using UnityEngine;
using System.Collections;
using System;

public class BGTiler : MonoBehaviour {
	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
            int tilesize = 6;
            GameObject[] prevtiles = GameObject.FindGameObjectsWithTag("bgtile");
            for(var i = 0 ; i < prevtiles.Length ; i ++)
            {
                Destroy(prevtiles[i]);
            }
            #I am aware the above is horrible practise, but this is quick and dirty.
            int h = Screen.height;
            int w = Screen.width;
            int top = (int) Math.Floor((Camera.main.transform.position.y-h/2)/tilesize) * tilesize;
            int left = (int) Math.Floor((Camera.main.transform.position.x-w/2)/tilesize) * tilesize;
            Debug.Log("Top is " + (top));
            Debug.Log("Left is " + (left));
            GameObject newtile;
            for (var x = left; x < left + w; x += tilesize){
                for (var y = top; y < top + h; y += tilesize){
                    newtile = GameObject.Instantiate(Resources.Load("BGTile")) as GameObject;
                    newtile.transform.position = new Vector3(x, y, 1);
                }
            }
	}
}
