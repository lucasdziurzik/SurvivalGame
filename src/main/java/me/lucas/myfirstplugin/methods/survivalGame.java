package me.lucas.myfirstplugin.methods;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import me.lucas.myfirstplugin.SurvivalGamePlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.*;


public class survivalGame {
    /* CONFIGURATION */
    private final int MAXWAVE = 10;
    private final int MOBS_ADDED_EVERY_WAVE = 2;
    private final double WAVE_DAMAGE_MULTIPLIER = 1.1;
    private final double ZOMBIE_DAMAGE = 1;
    private final double SKELETON_DAMAGE = 1;
    private final double SPIDER_DAMAGE = 1;
    private final double CAVE_SPIDER_DAMAGE = 1;
    private final double BLAZE_DAMAGE = 5;
    private List<Integer> killMilestones = new ArrayList<Integer>(Arrays.asList(10, 20, 30));

    private final String SWORD_NAME = ChatColor.GOLD + "MONSTER KILLER";
    private final String BOW_NAME = ChatColor.GOLD + "MONSTER SHOOTER";
    private final String WAND_NAME = ChatColor.RED + "HEALING WAND";


    private int _currentWave;
    private World _world;
    private List<Player> _listOfPlayersPlaying;
    private List<Player> _listOfPlayersAlive;
    private List<Player> _listOfPlayersDead;
    private Location _playersSpawnLocation;
    private final ChatColor _prefixColor = ChatColor.RED;
    private final ChatColor _gameTextColor = ChatColor.BLUE;
    private Map<String, Integer> _killScores;
    private List<Integer> _activeMonstersIds;

    private utilitymethods utility = new utilitymethods();
    private entitygenerator entitygenerator;


    public survivalGame(World world, Server server) {
        _world = world;
        entitygenerator = new entitygenerator(_world);


        _playersSpawnLocation = new Location(world, -118, 47,  -49);
        _listOfPlayersDead = new ArrayList<Player>();
        _listOfPlayersPlaying = new ArrayList<Player>();
        _listOfPlayersAlive = new ArrayList<Player>();
        _activeMonstersIds = new ArrayList<Integer>();
        _killScores = new HashMap<String, Integer>();

        for (Player player : server.getOnlinePlayers())
        {
            _listOfPlayersPlaying.add(player);
            _listOfPlayersAlive.add(player);
            _killScores.put(player.getName(), 0);

        }
        _currentWave = 1;

        SurvivalGamePlugin._currentGame = this;
    }

    /* Triggered at the beginning of the games and does several things
    * => Removing all monsters from the world
    * Giving every player the starting gear
    * Healing all players
    * Starting the first wave */
    public void StartGame() throws InterruptedException {
        int countdown = 4;
        SendMessageToPlayers("The game is starting ! Teleporting in " + (countdown + 1) + "...", "all");

        while (countdown > 0) {
            SendMessageToPlayers(countdown + "...", "all");
            Thread.sleep(1000);
            countdown--;
        }

        for (Entity entity : _world.getEntities())
        {
            if (!(entity instanceof Player))
                entity.remove();
        }

        for (Player player : _listOfPlayersPlaying) {
            player.teleport(_playersSpawnLocation);
            player.getInventory().clear();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1, 1000));

            ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
            ItemMeta swordMeta = sword.getItemMeta();
            swordMeta.setDisplayName(SWORD_NAME);
            swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            List<String> swordLore = new ArrayList<>();
            swordLore.add("Lorem ipsum and");
            swordLore.add("I don't remember the rest");
            swordMeta.setLore(swordLore);
            sword.setItemMeta(swordMeta);
            player.getInventory().setItem(0, sword);

            ItemStack wand = new ItemStack(Material.STICK, 1);
            ItemMeta wandMeta = sword.getItemMeta();
            wandMeta.setDisplayName(WAND_NAME);
            List<String> wandLore = new ArrayList<>();
            wandLore.add("Right click on this wand to");
            wandLore.add("heal 2 hearts !");
            wandMeta.setLore(wandLore);
            wand.setItemMeta(wandMeta);
            player.getInventory().setItem(8, wand);
        }
        HealAndFeedAllPlayers();
        SendMessageToPlayers("The game has started !", "all");
        UpdateGameScoreboard();
        this.StartNextWave();
    }


    /* Triggered when a new wave starts. Spawns all the mobs of that wave */
    public void StartNextWave() {
        UpdateGameScoreboard();
        HealAndFeedAllPlayers();
        String waveStartMessage = "Wave " + _currentWave + " has started ! ";
        if (_currentWave > 1)
            waveStartMessage += "Monsters now deal 5% more damage...";
        SendMessageToPlayers( waveStartMessage, "all");
        for (int i = 0; i < MOBS_ADDED_EVERY_WAVE * _currentWave; i++) {
            _activeMonstersIds.add(entitygenerator.generateCustomEntity(_currentWave));
        }
        if (_currentWave > 3)
            _activeMonstersIds.add(entitygenerator.generateBoss(_currentWave));
    }

    public void HealAndFeedAllPlayers() {
        for (Player player : _listOfPlayersAlive) {
            player.setHealth(20);
            player.setFoodLevel(20);
        }
    }


    /* Damage taken by the player depends on the type of mob that hit it, and is increased when the game progresses */
    public double CalculateDamageOnPlayer(EntityType damagerType) {
        double multiplier = 1.3 * Math.pow(WAVE_DAMAGE_MULTIPLIER, _currentWave);
        switch (damagerType) {
            case ZOMBIE:
                return ZOMBIE_DAMAGE * multiplier;
            case SKELETON:
            case ARROW:
                return SKELETON_DAMAGE * multiplier;
            case SPIDER:
                return SPIDER_DAMAGE * multiplier;
            case CAVE_SPIDER:
                return CAVE_SPIDER_DAMAGE * multiplier;
            case BLAZE:
                return BLAZE_DAMAGE;
            default:
                return 1;
        }
    }


    /* Increases the score of a player
    * If a player reaches a kill milestone, calls the function to upgrade the player*/
    public void AddKillPoint(String playerName) {
        int currentScore = _killScores.get(playerName) + 1;
        _killScores.remove(playerName);
        _killScores.put(playerName, currentScore);
        UpdateGameScoreboard();

        if (killMilestones.contains(currentScore))
            UpgradePlayer(playerName, currentScore);
    }

    /* TODO : see if it is possible to add it in the config file */
    /* If the player reaches a kills milestone, gives them a gear upgrade */
    public void UpgradePlayer(String playerName, int kills) {
        Player player = Bukkit.getPlayer(playerName);
        if (kills == killMilestones.get(0))
        {
            ItemStack[] items = player.getInventory().getContents();
            for (ItemStack itemstack : items) {
                if (itemstack != null && itemstack.getItemMeta().getDisplayName() == SWORD_NAME)
                {
                    ItemMeta meta = itemstack.getItemMeta();
                    meta.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
                    itemstack.setItemMeta(meta);
                }
            }
            SendMessageToPlayers(playerName + " has reached "+ killMilestones.get(0) +" kills ! They received a sword upgrade.", "all");

        } else if (kills == killMilestones.get(1)) {
            ItemStack bow = new ItemStack(Material.BOW, 1);
            ItemMeta bowMeta = bow.getItemMeta();
            bowMeta.setDisplayName(BOW_NAME);
            bow.setItemMeta(bowMeta);
            player.getInventory().setItem(1, bow);

            ItemStack arrows = new ItemStack(Material.ARROW, 64);
            player.getInventory().setItem(10, arrows);
            SendMessageToPlayers(playerName + " has reached "+ killMilestones.get(1) +" kills ! They received a new shiny bow.", "all");

        } else if (kills == killMilestones.get(2)) {
            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
            player.getInventory().setHelmet(helmet);
            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            player.getInventory().setChestplate(chestplate);
            ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
            player.getInventory().setLeggings(leggings);
            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
            player.getInventory().setBoots(boots);
            SendMessageToPlayers(playerName + " has reached "+ killMilestones.get(2) +" kills ! They received an ugly leather armor.", "all");

        }

    }

    public void ApplySlowness(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 4, 2));
        player.sendMessage(BuildGameMessage("The cave spider slowed you !"));
    }

    /* When a monster is killed, this function checks how many monsters remain and advance in the game if it was the last one for that wave */
    public void KillEntity(int entityId){
        _activeMonstersIds.remove(_activeMonstersIds.indexOf(entityId));
        if (_activeMonstersIds.size() == 0)
            Advance();
    }

    /* When a player dies, this function removes it from alive players and ends the game if it was the last survivor  */
    public void KillPlayer(Player playerKilled) {
        _listOfPlayersAlive.remove(playerKilled);
        SendMessageToPlayers(playerKilled.getName() + " has been killed !", "all");
        SendMessageToPlayers(_listOfPlayersPlaying.size() + " players remaining...", "all");
        _listOfPlayersDead.add(playerKilled);

        if (_listOfPlayersAlive.isEmpty())
            GameOver(false);
    }

    /* When all the monsters of the current wave have been killed, this function starts next game or ends the game if max wave was reached */
    public void Advance() {
        _currentWave++;
        if (_currentWave > MAXWAVE)
            GameOver(true);
        else
            StartNextWave();
    }

    /* Ends the game ! */
    public void GameOver(boolean win) {
        if (win)
            SendMessageToPlayers("Congratulations, you survived all " + MAXWAVE + " waves ! You won !" ,"all");
        else
            SendMessageToPlayers("Unfortunately all players died on wave " + _currentWave + ". Better luck next time !", "all");
    }

    /* Makes sure the entity was summoned by the game */
    public boolean CheckEntityIsInTheGame(int entityId) {
        return _activeMonstersIds.contains(entityId);
    }

    /* Makes sure the player is playing the game and was not killed yet */
    public boolean CheckPlayerIsInTheGameAndAlive(Player player) {
        return _listOfPlayersAlive.contains(player);
    }

    public void UpdateGameScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("Pannepixel", Criteria.DUMMY, ChatColor.BLUE + "PannePixel");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        ArrayList<Score> scoreboardLines = new ArrayList<>();

        scoreboardLines.add(objective.getScore("Current wave : " + _currentWave));
        scoreboardLines.add(objective.getScore("Ennemies left : " + _activeMonstersIds.size()));
        scoreboardLines.add(objective.getScore(" "));

        scoreboardLines.add(objective.getScore(ChatColor.BLUE + "Kills"));
        for (Map.Entry<String, Integer> pair : _killScores.entrySet())
            scoreboardLines.add(objective.getScore(ChatColor.GOLD + pair.getKey() + " " + ChatColor.DARK_PURPLE + pair.getValue()));
        scoreboardLines.add(objective.getScore("     "));

        Integer counter = scoreboardLines.size() + 1;

        for (Score score1 : scoreboardLines) {
            score1.setScore(counter);
            counter--;
        }

        for (Player player : _listOfPlayersPlaying) {
            System.out.println("Displaying scoreboard for " + player.getName());
            player.setScoreboard(scoreboard);

        }


    }


    /* UTILITIES */
    /* Depending on the value of option, you want to send the message to all players, only alive players or only dead players */
    public void SendMessageToPlayers(String message, String option) {
        List<Player> listOfPlayers = new ArrayList<Player>();
        switch (option) {
            case "all": listOfPlayers = _listOfPlayersPlaying;
                break;
            case "alive": listOfPlayers = _listOfPlayersAlive;
                break;
            case "dead": listOfPlayers = _listOfPlayersDead;
                break;
        }
        message = BuildGameMessage(message);

        for (Player player : listOfPlayers)
            player.sendMessage(message);
    }

    /* Applies the right formatting for the displayed message */
    private String BuildGameMessage(String message) {
        return _prefixColor + "[SurvivalGame] " + _gameTextColor + message;
    }


    }
