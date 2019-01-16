package client;

//import client.testPages.StarburstLineTest;
import client.testPages.A2Page3;
import geometry.Point2D;
import line.AlternatingLineRenderer;
//import line.ExpensiveLineRenderer;
import line.LineRenderer;
import client.ColoredDrawable;
import client.testPages.MeshPolygonTest;
//import client.testPages.ParallelogramTest;
//import client.testPages.RandomLineTest;
//import client.testPages.RandomPolygonTest;
//import client.testPages.StarburstPolygonTest;
import line.AntialiasingLineRenderer;
import line.BresenhamLineRenderer;
import line.DDALineRenderer;
import polygon.FilledPolygonRenderer;
import polygon.WireframePolygonRenderer;
import polygon.PolygonRenderer;
import windowing.PageTurner;
import windowing.drawable.*;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;
import client.interpreter.SimpInterpreter;

//import client.testPages.PolygonTest;

public class Client implements PageTurner {
	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int ARGB_GREEN = 0xff_00_ff_40;
	
	private static final int NUM_PAGES = 10;
	protected static final double GHOST_COVERAGE = 0.14;

	private static final int NUM_PANELS = 1;
	private static final Dimensions PANEL_SIZE = new Dimensions(650, 650);
	private static final Point2D[] lowCornersOfPanels = {
//			new Point2D( 50, 400),
//			new Point2D(400, 400),
			new Point2D( 50,  50),
//			new Point2D(400,  50),
	};
	
	private final Drawable drawable;
	private int pageNumber = 0;
	
	private Drawable image;
	private Drawable[] panels;
	private Drawable[] ghostPanels;					// use transparency and write only white
	private Drawable largePanel;
	private Drawable zBufferPanel;
//	private DepthCueingDrawable depthCueingDrawable;
	
	private LineRenderer[] lineRenderers;
	private PolygonRenderer[] polygonRenderers;

	private RendererTrio rendererTrio;
	private SimpInterpreter simpInterpreter;
	
	
	public Client(Drawable drawable) {
		this.drawable = drawable;	
		createDrawables();
		createRenderers();
	}

	public void createDrawables() {
		image = new InvertedYDrawable(drawable);
		image = new TranslatingDrawable(image, point(0, 0), dimensions(750, 750));
		image = new ColoredDrawable(image, ARGB_WHITE);
		
		largePanel = new TranslatingDrawable(image, point(50, 50),  dimensions(650, 650));

//		depthCueingDrawable = new

		createPanels();
		createGhostPanels();
		createZBufferPanel();

	}

	public void createPanels() {
		panels = new Drawable[NUM_PANELS];
		
		for(int index = 0; index < NUM_PANELS; index++) {
			panels[index] = new TranslatingDrawable(image, lowCornersOfPanels[index], PANEL_SIZE);
		}
	}

	private void createGhostPanels() {
		ghostPanels = new Drawable[NUM_PANELS];
		
		for(int index = 0; index < NUM_PANELS; index++) {
			Drawable drawable = panels[index];
			ghostPanels[index] = new GhostWritingDrawable(drawable, GHOST_COVERAGE);
		}
	}

	private void createZBufferPanel(){
		zBufferPanel = new ZBufferDrawable(panels[0]);
	}

	private Point2D point(int x, int y) {
		return new Point2D(x, y);
	}	
	private Dimensions dimensions(int x, int y) {
		return new Dimensions(x, y);
	}

	private void createRenderers() {
		
		lineRenderers = new LineRenderer[4];
		lineRenderers[0] = BresenhamLineRenderer.make();
//		lineRenderers[0] = ExpensiveLineRenderer.make();
		lineRenderers[1] = DDALineRenderer.make();
		lineRenderers[2] = AlternatingLineRenderer.make();
		lineRenderers[3] = AntialiasingLineRenderer.make();

		polygonRenderers = new PolygonRenderer[2];
		polygonRenderers[1] = FilledPolygonRenderer.make();
		polygonRenderers[0] = WireframePolygonRenderer.make();

		rendererTrio = new RendererTrio();
	}

	private boolean hasArgument;
	private String filename;

	public void setArgument(String[] argument){
		if (argument.length == 0) {
			hasArgument = false;
		}
		else {
			hasArgument = true;
			filename = argument[0];
		}
	}
	@Override
	public void nextPage() {
		if(hasArgument) {
			argumentNextPage();
		}
		else {
			noArgumentNextPage();
		}
	}

	private void argumentNextPage() {
		image.clear();
		largePanel.clear();
//		fullPanel.clear();
		zBufferPanel.clear();
		simpInterpreter = new SimpInterpreter(filename + ".simp", zBufferPanel, rendererTrio);
		simpInterpreter.interpret();
	}

	public void noArgumentNextPage() {
		System.out.println("PageNumber " + (pageNumber + 1));
		pageNumber = (pageNumber + 1) % NUM_PAGES;

		image.clear();
		largePanel.clear();
//		fullPanel.clear();
		String filename;

		switch(pageNumber) {
			case 1:  filename = "pageA";	 break;
			case 2:  filename = "pageB";	 break;
			case 3:	 filename = "pageC";	 break;
			case 4:  filename = "pageD";	 break;
			case 5:  filename = "pageE";	 break;
			case 6:  filename = "pageF";	 break;
			case 7:  filename = "pageG";	 break;
			case 8:  filename = "pageH";	 break;
			case 9:  filename = "pageI";	 break;
			case 0:  filename = "pageJ";	 break;

			default: defaultPage();
				return;
		}
		zBufferPanel.clear();
//		depthCueingDrawable = new DepthCueingDrawable(zBufferPanel, Color.WHITE);
		simpInterpreter = new SimpInterpreter(filename + ".simp", zBufferPanel, rendererTrio);
		simpInterpreter.interpret();
	}
	/*
	@Override
	public void nextPage() {
		System.out.println("PageNumber " + (pageNumber + 1));
		pageNumber = (pageNumber + 1) % NUM_PAGES;
		
		image.clear();
		largePanel.clear();
		switch(pageNumber) {
			case 1:
//				lineDrawerPage((panel, renderer)->{ new StarburstLineTest(panel, renderer); });//Assignment 1
				polygonDrawerPage(panels, polygonRenderers[0]);
				break;
			case 2:
				polygonDrawerPage(panels, polygonRenderers[1]);
//				lineDrawerPage((panel, renderer)->{ new ParallelogramTest(panel, renderer); });
				break;
			case 3:
				zBufferPanel.clear();
				page3(zBufferPanel, polygonRenderers[1]);
//				lineDrawerPage((panel, renderer)->{ new RandomLineTest(panel, renderer); });
				break;
			case 4:
				zBufferPanel.clear();
				depthCueingDrawable = new DepthCueingDrawable(zBufferPanel, Color.GREEN);
				simpInterpreter = new SimpInterpreter("pageA.simp", depthCueingDrawable, rendererTrio);
//				polygonDrawerPage(panels);
				break;
			case 5:
				zBufferPanel.clear();
				depthCueingDrawable = new DepthCueingDrawable(zBufferPanel, new Color(0.95, 0.95, 0.08));
				simpInterpreter = new SimpInterpreter("page5.simp", depthCueingDrawable, rendererTrio);
//				polygonDrawerPage(ghostPanels);
				break;
			case 6:
				zBufferPanel.clear();
				depthCueingDrawable = new DepthCueingDrawable(zBufferPanel, Color.WHITE);
				simpInterpreter = new SimpInterpreter("page6.simp", depthCueingDrawable, rendererTrio);
//				polygonDrawerPage1(panels);
				break;
			case 7:
				zBufferPanel.clear();
				depthCueingDrawable = new DepthCueingDrawable(zBufferPanel, Color.WHITE);
				simpInterpreter = new SimpInterpreter("page7.simp", depthCueingDrawable, rendererTrio);
				break;
			case 0:
				zBufferPanel.clear();
				depthCueingDrawable = new DepthCueingDrawable(zBufferPanel, Color.WHITE);
				simpInterpreter = new SimpInterpreter("page8.simp", depthCueingDrawable, rendererTrio);
				break;
			default:
				defaultPage();
				break;
		}
	}*/
	@FunctionalInterface
	private interface TestPerformer {
		public void perform(Drawable drawable, LineRenderer renderer);
	}
	private void lineDrawerPage(TestPerformer test) {
		image.clear();

		for(int panelNumber = 0; panelNumber < panels.length; panelNumber++) {
			panels[panelNumber].clear();
			test.perform(panels[panelNumber], lineRenderers[panelNumber]);
		}
	}
	public void polygonDrawerPage(Drawable[] panelArray, PolygonRenderer polygonRenderer) {
		image.clear();
		for(Drawable panel: panels) {		// 'panels' necessary here.  Not panelArray, because clear() uses setPixel.
			panel.clear();
		}
//		new StarburstPolygonTest(panelArray[0], polygonRenderer);
//		new MeshPolygonTest(panelArray[1], polygonRenderer, MeshPolygonTest.NO_PERTURBATION);
		new MeshPolygonTest(panelArray[0], polygonRenderer, MeshPolygonTest.USE_PERTURBATION);
//		new RandomPolygonTest(panelArray[3], polygonRenderer);
	}
	public void page3(Drawable panel, PolygonRenderer polygonRenderer){
		new A2Page3(panel, polygonRenderer);
	}
//
//	public void polygonDrawerPage1(Drawable[] panelArray) {
//		image.clear();
//		for(Drawable panel: panels) {		// 'panels' necessary here.  Not panelArray, because clear() uses setPixel.
//			panel.clear();
//		}
//		new PolygonTest(panelArray[0], polygonRenderer);
//	}

	private void defaultPage() {
		image.clear();
		largePanel.fill(ARGB_GREEN, Double.MAX_VALUE);
	}
}
