package com.JavaProject.Platform.Window;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import com.JavaProject.Platform.FrameWork.KeyInput;
import com.JavaProject.Platform.FrameWork.ObjectId;
import com.JavaProject.Platform.Objects.Block;
import com.JavaProject.Platform.Objects.Player;

public class Game extends Canvas implements Runnable
{

	private static final long serialVersionUID = -6629571940021294569L;

	private boolean running = false;
	private Thread thread;
	
	public static int WIDTH, HEIGHT;
	
	
	//Object
	Handler handler;
	Camera cam;
	
	private void init()
	{
		WIDTH = getWidth();
		HEIGHT = getHeight();
		
		handler = new Handler();
		
		cam = new Camera(0, 0);
		
		handler.addObject(new Player(100, 100, handler, ObjectId.Player));
		
		
		handler.createLevel();
		
		this.addKeyListener(new KeyInput(handler));
		
	}
	
	public synchronized void start()
	{
		if(running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void run()
	{
		init();
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountofTicks = 60.0;
		double ns = 1000000000 / amountofTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		while(running)
		{
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1)
			{
				tick();
				updates++;
				delta--;
			}
			render();
			frames++;
			
			if(System.currentTimeMillis() - timer > 1000)
			{
				timer += 1000;
				System.out.println("FPS: " + frames + " Ticks: " + updates);
				frames = 0;
				updates = 0;
			}
		}
	}
	
	private void tick()
	{
		handler.tick();
		for(int i = 0; i < handler.object.size(); i++)
		{
			if(handler.object.get(i).getId() == ObjectId.Player)
			{
				cam.tick(handler.object.get(i));
			}
		}
		
	}
	
	private void render()
	{
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null)
		{
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		
		
		//////////////////////////////////
		//Draw Here
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g2d.translate(cam.getX(), cam.getY()); //beggining of camera
		
		handler.render(g);
		
		g2d.translate(-cam.getX(), -cam.getY()); //end of camera
		
		//////////////////////////////////
		g.dispose();
		bs.show();
	}
	
	
	public static void main(String args[])
	{
		new Window(800, 600, "2D Platformer", new Game());
	}
}
