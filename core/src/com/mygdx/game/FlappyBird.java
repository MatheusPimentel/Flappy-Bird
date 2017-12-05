package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;

	private Texture[] passaro = new Texture[3];
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;

	private int pontuacao;

	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float velocidadeQueda = 0;
	private float posicaoPassaroVertical;
	private float posicaoMovimentoCanoHorizontal;
	private float espacoEntreCanos;
	private float alturaEntreCanosRandomica;
	private final float VIRTUAL_WIDTH = 768;
	private final float VIRTUAL_HEIGHT = 1024;

	private int estadoJogo = 0;
	private boolean marcouPonto = false;

	private BitmapFont fonte;
	private BitmapFont mensagem;

	private Circle passaroCirculo;
    private Random randomico;
    //Câmera
	private OrthographicCamera camera;
	private Viewport viewport;
//    private ShapeRenderer shapeRenderer;

	@Override
	public void create() {
		batch = new SpriteBatch();
		passaro[0] = new Texture("passaro1.png");
		passaro[1] = new Texture("passaro2.png");
		passaro[2] = new Texture("passaro3.png");
		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");
		gameOver = new Texture("game_over.png");
		randomico = new Random();
		fonte = new BitmapFont();
		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(3);
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(6);
		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoPassaroVertical = alturaDispositivo / 2;
		posicaoMovimentoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 300;
		passaroCirculo = new Circle();
//		retanguloCanoTopo = new Rectangle();
//		retangutoCanoBaixo = new Rectangle();
//		shapeRenderer = new ShapeRenderer();

		//configurações da camera
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
	}

	@Override
	public void render() {
	    camera.update();

	    //limpar frames
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        float deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 10;

        if (variacao > 2) {
            variacao = 0;
        }

        if (estadoJogo == 0) {

            if (Gdx.input.justTouched()) {
                estadoJogo = 1;
            }
        } else {
            velocidadeQueda += 0.3;
            if (posicaoPassaroVertical > 0 || velocidadeQueda < 0) {
                posicaoPassaroVertical -= velocidadeQueda;
            }

            if (estadoJogo == 1) {
                posicaoMovimentoCanoHorizontal -= deltaTime * 200;

                if (Gdx.input.justTouched()) {
                    velocidadeQueda = -10;
                }

                //verifica se o cano saiu inteiramente da tela
                if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomica = randomico.nextInt(400) - 200;
                    marcouPonto = false;
                }

                //verifica pontuacao
                if (posicaoMovimentoCanoHorizontal < 120) {
                    if (!marcouPonto) {
                        pontuacao++;
                        marcouPonto = true;
                    }
                }
            } else {
                //tela game-over

                if (Gdx.input.justTouched()) {
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoPassaroVertical = alturaDispositivo / 2;
                }
            }
        }

        //configurar projeção da camera
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
		batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 +
                alturaEntreCanosRandomica);
		batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() -
                espacoEntreCanos / 2 + alturaEntreCanosRandomica);
        batch.draw(passaro[(int) variacao], 120, posicaoPassaroVertical);
        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50);

        if (estadoJogo == 2) {
            batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
            mensagem.draw(batch,"Toque para reiniciar", larguraDispositivo / 2 - 200,
                    alturaDispositivo / 2 - gameOver.getHeight() / 2);
        }
		batch.end();

		passaroCirculo.set(120 + passaro[0].getWidth() / 2, posicaoPassaroVertical + passaro[0].getHeight() / 2,
                passaro[0].getWidth() / 2);

		Rectangle retangutoCanoBaixo = new Rectangle(
				posicaoMovimentoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica,
				canoBaixo.getWidth(),
				canoBaixo.getHeight());

		Rectangle retanguloCanoTopo = new Rectangle(
				posicaoMovimentoCanoHorizontal,
				alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica,
				canoTopo.getWidth(),
				canoTopo.getHeight());
		//desenhar formas
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
//        shapeRenderer.rect(retangutoCanoBaixo.x, retangutoCanoBaixo.y, retangutoCanoBaixo.width, retangutoCanoBaixo.height);
//        shapeRenderer.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.end();

        //teste de colisao
        if (Intersector.overlaps(passaroCirculo, retangutoCanoBaixo) || Intersector.overlaps(passaroCirculo, retanguloCanoTopo)
                || posicaoPassaroVertical <= 0 || posicaoPassaroVertical >= alturaDispositivo) {
            estadoJogo = 2;
        }
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}