package me.lucas.myfirstplugin.methods;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/* Everything related to generating entities */
public class entitygenerator {
    private World _world;
    private List<Location> _monsterSpawnLocations;

    public entitygenerator(World world) {
        _world = world;
        _monsterSpawnLocations = new ArrayList<>();

        _monsterSpawnLocations.add(new Location(world, -104, 50, -49));
        _monsterSpawnLocations.add(new Location(world, -107, 53, -36));
        _monsterSpawnLocations.add(new Location(world, -100, 53, -38));
        _monsterSpawnLocations.add(new Location(world, -122, 45, -49));
    }


/* Main function to generate the monster */
    public Integer generateCustomEntity(int currentWave) {
        EntityType entityType = getRandomEntityType();
        int waveSpeedLevel = getSpeedLevel(currentWave);
        int bonusSpeedLevel = (getRandomBonus(1, 3) ? 1 : 0);


        Location spawnLocation = getRandomSpawnLocation();
        Monster newMonster = (Monster) _world.spawnEntity(spawnLocation, entityType);
        newMonster.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, waveSpeedLevel + bonusSpeedLevel));
        newMonster.setCustomName(generateCustomEntityName(entityType, bonusSpeedLevel, currentWave));
        newMonster.setCustomNameVisible(true);

        return newMonster.getEntityId();
    }

    public Integer generateBoss(int currentWave) {
        EntityType entityType = EntityType.BLAZE;
        Location spawnLocation = getRandomSpawnLocation();
        Monster newMonster = (Monster) _world.spawnEntity(spawnLocation, entityType);
        newMonster.setCustomName(generateCustomEntityName(entityType, 0, currentWave));
        newMonster.setCustomNameVisible(true);

        return newMonster.getEntityId();
    }

    /* Every mob summoned has a random type chosen by this function */
    private EntityType getRandomEntityType() {
        Random random = new Random();
        int randomInt = random.nextInt(100);
        if (randomInt < 35)
            return EntityType.ZOMBIE;
        if (randomInt < 65)
            return EntityType.SKELETON;
        if (randomInt < 95)
            return EntityType.SPIDER;
        return EntityType.CAVE_SPIDER;
    }

    /* The higher the wave is, the faster the mob is */
    private int getSpeedLevel(int currentWave) {
        if (currentWave < 2)
            return 1;
        if (currentWave < 4)
            return 2;
        return 3;
    }


    /* Mobs can spawn with a custom speed bonus */
    private Boolean getRandomBonus(Integer digit1, Integer digit2) {
        Random random = new Random();
        int randomInt = random.nextInt(digit2);
        return (randomInt > digit1);
    }




    /* Current entity name template : [XXX] (Speedy) MobType */
    private String generateCustomEntityName(EntityType entityType, Integer speedy, int currentWave) {
        Integer mobLevel = currentWave * 100;
        if (speedy > 0)
            mobLevel += 10;
        String result = ChatColor.BLUE + "" + ChatColor.BOLD + "[" + mobLevel + "] " + ChatColor.WHITE + "" + ChatColor.BOLD;
        if (speedy > 0)
            result += "Speedy ";

        switch (entityType) {
            case ZOMBIE: result += "Zombie";
            break;
            case SKELETON: result += "Skeleton";
            break;
            case SPIDER: result += "Spooder";
            break;
            case CAVE_SPIDER: result += "Slowing Spooder";
            break;
            case BLAZE: result += "STRONG DEADLY BLAZE";
            break;
        }
        return result;
    }

    private Location getRandomSpawnLocation() {
        Random random = new Random();
        int randomInt = random.nextInt(_monsterSpawnLocations.size());
        return _monsterSpawnLocations.get(randomInt);
    }


}
