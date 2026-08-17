package com.modulo08.towerdefense.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.modulo08.towerdefense.Main;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.modulo08.towerdefense.entities.Tower;
import com.modulo08.towerdefense.entities.TowerType;
import com.modulo08.towerdefense.entities.Projectile;
import com.modulo08.towerdefense.entities.EnemyType;
import com.modulo08.towerdefense.entities.Enemy;
import com.modulo08.towerdefense.systems.BuildSlot;
import com.modulo08.towerdefense.systems.InputUtils;
import com.modulo08.towerdefense.systems.LevelConfig;
import com.modulo08.towerdefense.systems.Wave;
import com.modulo08.towerdefense.systems.WaveManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameScreen extends InputAdapter implements Screen {

    private static final float VIEWPORT_WIDTH = 20f;
    private static final float VIEWPORT_HEIGHT = 12f;
    private static final int OURO_INICIAL = 100;
    private static final float TAMANHO_INIMIGO = 0.5f;
    private static final float TAMANHO_TORRE = 0.8f;
    private static final float TAMANHO_SLOT = 0.5f;
    private static final float TAMANHO_PROJETIL = 0.2f;
    private static final int TILES_SIZE_PIXELS = 32;
    private static final float TAMANHO_TILE_MUNDO = 1f;
    private static final int GRID_COLUNAS = (int) VIEWPORT_WIDTH;
    private static final int GRID_LINHAS = (int) VIEWPORT_HEIGHT;
    private static final float VELOCIDADE_PROJEITL_PADRAO = 8f;

    // Aumentado para facilitat cliques em telas de alta resolução!
    private static final float TOLERANCIA_SLOT = 0.8f;

    private final Main game;
    private final List<Enemy> inimigos = new ArrayList<>();
    private final List<Tower> torres = new ArrayList<>();
    private final List<Projectile> projeteisAtivos = new ArrayList<>();
    private final List<BuildSlot> slotConstrucao = new ArrayList<>();
    private List<Vector2> waypoints;
    private WaveManager waveManager;
    private Viewport viewport;
    private OrthographicCamera hudCamera;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Texture pixel;
    private Texture circuloAlcance;
    private Texture moneyTexture;

    private BitmapFont fonte;
    private Texture tilesetTexture;
    private TextureRegion tileGrama;
    private TextureRegion tileCaminho;
    private TextureRegion tileSlotLivre;
    private boolean[][] celulasCaminho;
    private int ouro;

    private TowerType tipoTorreSelecionada = TowerType.BASICA;
    private LevelConfig mapaAtual = LevelConfig.MAPA_1;
    private BuildSlot slotSelecionado = null;


    private final Map<TowerType, Animation<TextureRegion>> animacoesIdle = new HashMap<>();
    private final Map<TowerType, Animation<TextureRegion>> animacoesAtaque = new HashMap<>();

    private final Map<EnemyType, Animation<TextureRegion>> animacoesInimigos = new HashMap<>();

    public GameScreen(Main game) { this.game = game; }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        camera.position.set(VIEWPORT_WIDTH / 2f, VIEWPORT_HEIGHT / 2f, 0);
        camera.update();

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch = new SpriteBatch();
        pixel = criarTexturaPixel();
        circuloAlcance = criarTexturaCirculo();


        if (Gdx.files.internal("money.png").exists()) {
            moneyTexture = new Texture(Gdx.files.internal("money.png"));
        } else {
            moneyTexture = pixel;
        }

        fonte = new BitmapFont();

        tilesetTexture = new Texture(Gdx.files.internal("tileset.png"));
        tilesetTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegion[][] gridTileset = TextureRegion.split(tilesetTexture, TILES_SIZE_PIXELS, TILES_SIZE_PIXELS);
        tileGrama = gridTileset[0][0];
        tileCaminho = gridTileset[0][1];
        tileSlotLivre = gridTileset[1][0];

        carregarAnimacoesTorres();
        carregarAnimacoesInimigos();


        iniciarMapa();

        Gdx.input.setInputProcessor(this);
    }

    // NOVO MÉTODO: Centraliza o setup do nível para poder ser chamado ao trocar de mapa
    private void iniciarMapa() {
        inimigos.clear();
        torres.clear();
        projeteisAtivos.clear();
        slotConstrucao.clear();
        slotSelecionado = null;

        ouro = OURO_INICIAL;

        waypoints = mapaAtual.getWaypoints();
        celulasCaminho = calcularCelulasDoCaminho();

        for (Vector2 posicaoSlot : mapaAtual.getPosicoesDosSlots()) {
            slotConstrucao.add(new BuildSlot(posicaoSlot));
        }

        waveManager = new WaveManager(inimigos, waypoints);
        waveManager.addWave(new Wave(5, 1.2f, EnemyType.BASICO));
        waveManager.addWave(new Wave(4, 1.5f, EnemyType.EXPLOSIVO));
        waveManager.addWave(new Wave(8, 1.0f, EnemyType.RAPIDO));
        waveManager.addWave(new Wave(6, 1.5f, EnemyType.TANQUE));
        waveManager.addWave(new Wave(10, 1.2f, EnemyType.BASICO));
        waveManager.addWave(new Wave(8, 1.5f, EnemyType.EXPLOSIVO));
        waveManager.addWave(new Wave(16, 1.0f, EnemyType.RAPIDO));
        waveManager.addWave(new Wave(12, 1.5f, EnemyType.TANQUE));
        waveManager.addWave(new Wave(5, 1.2f, EnemyType.TANQUE));
        waveManager.addWave(new Wave(4, 1.5f, EnemyType.EXPLOSIVO));
    }

    private void carregarAnimacoesTorres() {
        Texture texIdle1 = new Texture("torre_basica_idle.png");
        Texture texAtaque1 = new Texture("torre_basica_ataque.png");
        Texture texIdle2 = new Texture("torre_atirador_idle.png");
        Texture texAtaque2 = new Texture("torre_atirador_ataque.png");
        Texture texIdle3 = new Texture("torre_metra_idle.png");
        Texture texAtaque3 = new Texture("torre_metra_ataque.png");
        Texture texIdle4 = new Texture("torre_melee_idle.png");
        Texture texAtaque4 = new Texture("torre_melee_ataque.png");
        TextureRegion[][] framesIdle1 = TextureRegion.split(texIdle1, 32, 32);
        TextureRegion[][] framesAtaque1 = TextureRegion.split(texAtaque1, 32, 32);
        TextureRegion[][] framesIdle2 = TextureRegion.split(texIdle2, 32, 32);
        TextureRegion[][] framesAtaque2 = TextureRegion.split(texAtaque2, 32, 32);
        TextureRegion[][] framesIdle3 = TextureRegion.split(texIdle3, 32, 32);
        TextureRegion[][] framesAtaque3 = TextureRegion.split(texAtaque3, 32, 32);
        TextureRegion[][] framesIdle4 = TextureRegion.split(texIdle4, 32, 32);
        TextureRegion[][] framesAtaque4 = TextureRegion.split(texAtaque4, 32, 32);
        animacoesIdle.put(TowerType.BASICA, new Animation<TextureRegion>(0.15f, framesIdle1[0]));
        animacoesAtaque.put(TowerType.BASICA, new Animation<TextureRegion>(0.15f, framesAtaque1[0]));
        animacoesIdle.put(TowerType.FRANCO_ATIRADOR, new Animation<TextureRegion>(0.15f, framesIdle2[0]));
        animacoesAtaque.put(TowerType.FRANCO_ATIRADOR, new Animation<TextureRegion>(0.15f, framesAtaque2[0]));
        animacoesIdle.put(TowerType.METRALHADORA, new Animation<TextureRegion>(0.15f, framesIdle3[0]));
        animacoesAtaque.put(TowerType.METRALHADORA, new Animation<TextureRegion>(0.15f, framesAtaque3[0]));
        animacoesIdle.put(TowerType.MELEE, new Animation<TextureRegion>(0.15f, framesIdle4[0]));
        animacoesAtaque.put(TowerType.MELEE, new Animation<TextureRegion>(0.15f, framesAtaque4[0]));
    }

    private void carregarAnimacoesInimigos() {
        Texture texBasico = new Texture("inimigo_basico.png");
        Texture texExplosivo = new Texture("inimigo_explosivo.png");
        Texture texRapido = new Texture("inimigo_rapido.png");
        Texture texTanque = new Texture("inimigo_tanque.png");

        TextureRegion[][] framesBasico = TextureRegion.split(texBasico, 32, 32);
        TextureRegion[][] framesExplosivo = TextureRegion.split(texExplosivo, 32, 32);
        TextureRegion[][] framesRapido = TextureRegion.split(texRapido, 32, 32);
        TextureRegion[][] framesTanque = TextureRegion.split(texTanque, 32, 32);

        animacoesInimigos.put(EnemyType.BASICO, new Animation<TextureRegion>(0.15f, framesBasico[0]));
        animacoesInimigos.put(EnemyType.EXPLOSIVO, new Animation<TextureRegion>(0.15f, framesExplosivo[0]));
        animacoesInimigos.put(EnemyType.RAPIDO, new Animation<TextureRegion>(0.15f, framesRapido[0]));
        animacoesInimigos.put(EnemyType.TANQUE, new Animation<TextureRegion>(0.15f, framesTanque[0]));
    }

    private Texture criarTexturaPixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture criarTexturaCirculo() {
        int tamanho = 128;
        Pixmap pixmap = new Pixmap(tamanho, tamanho, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawCircle(tamanho / 2, tamanho / 2, (tamanho / 2) - 2);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.NUM_1 || keycode == Input.Keys.NUMPAD_1) { tipoTorreSelecionada = TowerType.BASICA; return true; }
        else if (keycode == Input.Keys.NUM_2 || keycode == Input.Keys.NUMPAD_2) { tipoTorreSelecionada = TowerType.FRANCO_ATIRADOR; return true; }
        else if (keycode == Input.Keys.NUM_3 || keycode == Input.Keys.NUMPAD_3) { tipoTorreSelecionada = TowerType.METRALHADORA; return true; }
        else if (keycode == Input.Keys.NUM_4 || keycode == Input.Keys.NUMPAD_4) { tipoTorreSelecionada = TowerType.MELEE; return true; }
        else if (keycode == Input.Keys.M) {
            LevelConfig[] mapas = LevelConfig.values();
            int proximoIndice = (mapaAtual.ordinal() + 1) % mapas.length;
            mapaAtual = mapas[proximoIndice];
            iniciarMapa();
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 mundo = InputUtils.screenToWorld(camera, screenX, screenY);
        Vector2 posicaoClique = new Vector2(mundo.x, mundo.y);

        BuildSlot slot = encontrarSlotProximo(posicaoClique);
        slotSelecionado = slot;

        if (slot == null || !slot.estaLivre()) return true;
        if (ouro < tipoTorreSelecionada.getCusto()) return true;

        Tower torre = new Tower(slot.getPosicao(), tipoTorreSelecionada, VELOCIDADE_PROJEITL_PADRAO);
        slot.construir(torre);
        torres.add(torre);
        ouro -= tipoTorreSelecionada.getCusto();
        return true;
    }

    @Override
    public void render(float delta) {
        waveManager.update(delta);
        for (Enemy inimigo : inimigos) { inimigo.moveTowardsWaypoint(delta); }
        for (Tower torre : torres) { torre.update(delta, inimigos, projeteisAtivos); }
        atualizarProjeteis(delta);

        Iterator<Enemy> iterator = inimigos.iterator();
        while (iterator.hasNext()) {
            Enemy inimigo = iterator.next();
            inimigo.verificarExplosaoBoomer(torres);
            if (inimigo.estaMorto() || inimigo.chegouAoFim()) {
                if (inimigo.estaMorto()) ouro += inimigo.getRecompensa();
                iterator.remove();
            }
        }

        ScreenUtils.clear(Color.BLACK);
        camera.update();

        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        desenharTerreno();
        desenharSlots();
        desenharInimigos();
        desenharTorres();
        desenharProjeteis();
        desenharIndicadorAlcance(); // Desenha raio vermelho

        batch.setProjectionMatrix(hudCamera.combined);
        desenharHud();
        batch.end();
    }

    private void atualizarProjeteis(float delta) {
        Iterator<Projectile> iterator = projeteisAtivos.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.update(delta);
            if (projectile.alvoMorreu()) { iterator.remove(); continue; }
            if (projectile.atingiuAlvo()) { projectile.aplicarDanoNoAlvo(); iterator.remove(); }
        }
    }

    private void desenharTerreno() {
        batch.setColor(Color.WHITE);
        for (int linha = 0; linha < GRID_LINHAS; linha++) {
            for (int coluna = 0; coluna < GRID_COLUNAS; coluna++) {
                TextureRegion tile = celulasCaminho[linha][coluna] ? tileCaminho : tileGrama;
                batch.draw(tile, coluna * TAMANHO_TILE_MUNDO, linha * TAMANHO_TILE_MUNDO, TAMANHO_TILE_MUNDO, TAMANHO_TILE_MUNDO);
            }
        }
    }

    private void desenharSlots() {
        batch.setColor(Color.WHITE);
        for (BuildSlot slot : slotConstrucao) {
            if (!slot.estaLivre()) continue;
            Vector2 posicao = slot.getPosicao();
            batch.draw(tileSlotLivre, posicao.x - TAMANHO_SLOT / 2F, posicao.y - TAMANHO_SLOT / 2F, TAMANHO_SLOT, TAMANHO_SLOT);
        }
    }

    private void desenharInimigos() {
        for (Enemy inimigo : inimigos) {
            Vector2 posicao = inimigo.getPosicao();
            // Busca a animação correspondente ao tipo do inimigo
            Animation<TextureRegion> anim = animacoesInimigos.get(inimigo.getTipo());

            // Se a animação for encontrada, desenha o frame atual
            if (anim != null) {
                batch.setColor(Color.WHITE);
                TextureRegion frameAtual = anim.getKeyFrame(inimigo.getStateTime(), true);
                batch.draw(frameAtual, posicao.x - TAMANHO_INIMIGO / 2F, posicao.y - TAMANHO_INIMIGO / 2F, TAMANHO_INIMIGO, TAMANHO_INIMIGO);
            } else {
                // Fallback padrão limpo (quadrado vermelho) caso a textura não exista
                batch.setColor(Color.RED);
                batch.draw(pixel, posicao.x - TAMANHO_INIMIGO / 2F, posicao.y - TAMANHO_INIMIGO / 2F, TAMANHO_INIMIGO, TAMANHO_INIMIGO);
            }
        }
    }

    private void desenharTorres() {
        for (Tower torre : torres) {
            Vector2 posicao = torre.getPosicao();
            Animation<TextureRegion> anim = (torre.getEstadoAtual() == Tower.State.ATTACKING) ? animacoesAtaque.get(torre.getTipo()) : animacoesIdle.get(torre.getTipo());

            // Se você tiver a animação configurada, desenha o sprite!
            if (anim != null) {
                batch.setColor(Color.WHITE);
                TextureRegion frameAtual = anim.getKeyFrame(torre.getStateTime(), true);
                batch.draw(frameAtual, posicao.x - TAMANHO_TORRE / 2F, posicao.y - TAMANHO_TORRE / 2F, TAMANHO_TORRE, TAMANHO_TORRE);
            } else {
                // Senão usa o fallback padrão limpo
                batch.setColor(torre.estaStunada() ? Color.GRAY : Color.BLUE);
                batch.draw(pixel, posicao.x - TAMANHO_TORRE / 2F, posicao.y - TAMANHO_TORRE / 2F, TAMANHO_TORRE, TAMANHO_TORRE);
            }

            if (torre.estaStunada()) {
                batch.setColor(Color.YELLOW);
                batch.draw(pixel, posicao.x - 0.1f, posicao.y + TAMANHO_TORRE / 2F + 0.05f, 0.2f, 0.2f);
            }
        }
    }

    private void desenharProjeteis() {
        batch.setColor(Color.YELLOW);
        for (Projectile projectile : projeteisAtivos) {
            Vector2 posicao = projectile.getPosicao();
            batch.draw(pixel, posicao.x - TAMANHO_PROJETIL / 2F, posicao.y - TAMANHO_PROJETIL / 2F, TAMANHO_PROJETIL, TAMANHO_PROJETIL);
        }
    }

    private void desenharIndicadorAlcance() {
        if (slotSelecionado == null) return;

        float raio = tipoTorreSelecionada.getRange();
        if (!slotSelecionado.estaLivre() && slotSelecionado.getTorre() != null) {
            raio = slotSelecionado.getTorre().getRange();
        }

        Vector2 pos = slotSelecionado.getPosicao();

        // Transparência e Cor para o Indicador de Range
        batch.setColor(1f, 0f, 0f, 0.8f);
        batch.draw(circuloAlcance, pos.x - raio, pos.y - raio, raio * 2f, raio * 2f);
        batch.setColor(Color.WHITE);
    }

    private void desenharHud() {
        batch.setColor(Color.WHITE);

        // Desenha Ícone de Dinheiro (money.png)
        batch.draw(moneyTexture, 10, hudCamera.viewportHeight - 32, 24, 24);
        fonte.draw(batch, ": " + ouro, 38, hudCamera.viewportHeight - 14);

        // Indicador de Wave
        int waveAtualExibicao = waveManager.getWaveAtual() + 1;
        int totalWaves = waveManager.getWaves().size();
        fonte.draw(batch, "Wave: " + waveAtualExibicao + " / " + totalWaves, 150, hudCamera.viewportHeight - 14);

        // Indicador do Mapa Atual
        fonte.draw(batch, "Mapa Atual (Aperte M): " + mapaAtual.name(), 350, hudCamera.viewportHeight - 14);

        // Indicador do Menu de Torres selecionada
        fonte.draw(batch, "Torre [1-4]: " + tipoTorreSelecionada.name() + " ($" + tipoTorreSelecionada.getCusto() + ")", 10, hudCamera.viewportHeight - 45);
    }

    private boolean[][] calcularCelulasDoCaminho() {
        boolean[][] celulas = new boolean[GRID_LINHAS][GRID_COLUNAS];
        for (int i = 0; i < waypoints.size() - 1; i++) { marcarSegmento(celulas, waypoints.get(i), waypoints.get(i + 1)); }
        return celulas;
    }

    private void marcarSegmento(boolean[][] celulas, Vector2 a, Vector2 b) {
        if (a.y == b.y) {
            int linha = clampIndice((int) a.y, GRID_LINHAS);
            int colInicio = clampIndice((int) Math.min(a.x, b.x), GRID_COLUNAS);
            int colFim = clampIndice((int) Math.max(a.x, b.x), GRID_COLUNAS);
            for (int coluna = colInicio; coluna <= colFim; coluna++) { celulas[linha][coluna] = true; }
        } else {
            int coluna = clampIndice((int) a.x, GRID_COLUNAS);
            int linhaInicio = clampIndice((int) Math.min(a.y, b.y), GRID_LINHAS);
            int linhaFim = clampIndice((int) Math.max(a.y, b.y), GRID_LINHAS);
            for (int linha = linhaInicio; linha <= linhaFim; linha++) { celulas[linha][coluna] = true; }
        }
    }

    private int clampIndice(int valor, int limite) { return Math.max(0, Math.min(valor, limite - 1)); }

    private BuildSlot encontrarSlotProximo(Vector2 posicaoClique) {
        BuildSlot maisProximo = null;
        float menorDistancia = TOLERANCIA_SLOT;
        for (BuildSlot slot : slotConstrucao) {
            float distancia = slot.getPosicao().dst(posicaoClique);
            if (distancia <= menorDistancia) { menorDistancia = distancia; maisProximo = slot; }
        }
        return maisProximo;
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); hudCamera.setToOrtho(false, width, height); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        batch.dispose();
        pixel.dispose();
        circuloAlcance.dispose();
        if (moneyTexture != pixel) moneyTexture.dispose();
        fonte.dispose();
        tilesetTexture.dispose();
    }
}
