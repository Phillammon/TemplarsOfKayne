using UnityEngine;
using System.Collections;
using System;

public class PickTiler : MonoBehaviour {

	// Use this for initialization
	void Start () {
            int tilesize = 2;
            int top = (int) Math.Floor(Camera.main.ScreenToWorldPoint(new Vector3(0,0,0)).y/tilesize)*tilesize;
            int left = (int) Math.Floor(Camera.main.ScreenToWorldPoint(new Vector3(0,0,0)).x/tilesize)*tilesize;
            int bottom = (int) Math.Ceiling(Camera.main.ScreenToWorldPoint(new Vector3(0,Screen.height,0)).y/tilesize)*tilesize;
            int right = (int) Math.Ceiling(Camera.main.ScreenToWorldPoint(new Vector3(Screen.width,0,0)).x/tilesize)*tilesize;
            GameObject newtile;
            for (var x = left; x < right; x += tilesize){
                for (var y = top; y < bottom; y += tilesize){
                    newtile = GameObject.Instantiate(Resources.Load("BackWall")) as GameObject;
                    newtile.transform.position = new Vector3(x+tilesize/2, y+tilesize/2, 1);
                }
            }
	}
	
	// Update is called once per frame
	void Update () {
	}
}
