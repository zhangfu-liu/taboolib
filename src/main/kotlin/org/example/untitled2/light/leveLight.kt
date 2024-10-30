package org.example.untitled2.light  // 定义包名

// 导入需要的类和接口
import taboolib.common.platform.function.info
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.data.Powerable
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

// 定义主类为单例对象
object LevelLight {
    // 定义配置文件，使用TabooLib的配置系统
    @Config("levelLight.yml")
    lateinit var config: Configuration
        private set


    // 插件启用时初始化
    @Awake(LifeCycle.ENABLE)
    fun init() {
        info("[LevelLight] 插件正在初始化...")
        loadConfig()  // 加载配置
        info("[LevelLight] 初始化完成")
    }
    // 存储灯和拉杆的映射关系
    private val lights = mutableMapOf<String, Light>()
    private val levers = mutableMapOf<String, Lever>()

    // 定义灯的数据类，存储坐标信息
    data class Light(
        val x: Double,
        val y: Double,
        val z: Double
    )

    // 定义拉杆的数据类，存储坐标和控制的灯列表
    data class Lever(
        val x: Double,
        val y: Double,
        val z: Double,
        val controlLights: List<String>  // 存储被控制的灯的ID列表
    )

    // 加载配置文件
    fun loadConfig() {
        // 加载灯的配置
        config.getConfigurationSection("lights")?.getKeys(false)?.forEach { id ->
            // 获取每个灯的配置部分
            val section = config.getConfigurationSection("lights.$id") ?: return@forEach
            val locStr = section.getString("location") ?: return@forEach

            // 解析坐标 分割字符 并将字符串遍历 为double类型 随后给x y z
            val (x, y, z) = locStr.split("/").map { it.toDouble() }
            // 存储灯的信息
            lights[id] = Light(x, y, z)
            println("[LevelLight] 已加载灯 $id: 位置 $x/$y/$z")
        }

        // 加载拉杆的配置
        config.getConfigurationSection("levers")?.getKeys(false)?.forEach { id ->
            // 获取每个拉杆的配置部分
            val section = config.getConfigurationSection("levers.$id") ?: return@forEach
            val locStr = section.getString("location") ?: return@forEach
            val controlList = section.getStringList("control")  // 获取控制的灯列表

            // 解析坐标
            val (x, y, z) = locStr.split("/").map { it.toDouble() }
            // 存储拉杆信息
            levers[id] = Lever(x, y, z, controlList)
            println("[LevelLight] 已加载拉杆 $id: 位置 $x/$y/$z, 控制灯: $controlList")
        }
    }

    // 玩家加入服务器时的事件处理
    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // 获取玩家所在的世界
        val world = event.player.world
        println("[LevelLight] 玩家 ${event.player.name} 加入世界: ${world.name}")

        // 在玩家所在世界设置所有红石灯
        lights.forEach { (id, light) ->
            // 创建位置对象并设置方块
            val location = Location(world, light.x, light.y, light.z)
            val oldBlock = location.block.type
            location.block.type = Material.REDSTONE_LAMP
            println("[LevelLight] 设置灯 $id: 位置 ${light.x}/${light.y}/${light.z}")
            println("[LevelLight] 原方块类型: $oldBlock -> 现方块类型: ${location.block.type}")
        }

        // 检查并输出所有灯的状态
        println("[LevelLight] 检查所有灯的状态:")
        lights.forEach { (id, light) ->
            val location = Location(world, light.x, light.y, light.z)
            println("[LevelLight] 灯 $id: 位置 ${light.x}/${light.y}/${light.z} - 方块类型: ${location.block.type}")
        }
    }

    // 处理拉杆被拉动的事件
    @SubscribeEvent
    fun onLeverPull(event: PlayerInteractEvent) {
        // 检查是否是右键点击拉杆
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        val block = event.clickedBlock ?: return
        if (block.type != Material.LEVER) return

        // 获取拉杆的供电状态 将方块数据转换为 Powerable 接口类型
        val leverData = block.blockData as Powerable
        //获取供电状态，检查拉杆是否处于供电状态
        val isPowered = leverData.isPowered

        // 查找配置中对应的拉杆
        val lever = levers.entries.find {
            it.value.x == block.location.x &&
                    it.value.y == block.location.y &&
                    it.value.z == block.location.z
        } ?: return

        // 处理每个被控制的灯
        lever.value.controlLights.forEach { lightId ->
            lights[lightId]?.let { light ->
                // 获取灯的位置
                val lightLoc = Location(event.player.world, light.x, light.y, light.z)
                try {
                    //拿到灯的方块类型
                    val lightBlock = lightLoc.block
                    //判断是否是红石灯
                    if (lightBlock.type == Material.REDSTONE_LAMP) {
                        // 1. 获取红石灯的数据并转换为 Lightable 类型
                        val lightable = lightBlock.blockData as org.bukkit.block.data.Lightable
                        // 2. 设置红石灯的亮灭状态
                        lightable.isLit = isPowered
                        // 3. 将修改后的状态应用到方块上
                        lightBlock.setBlockData(lightable, false)
                        println("[LevelLight] 设置灯 $lightId 状态为: ${if (isPowered) "开启" else "关闭"}")
                    }
                } catch (e: Exception) {
                    println("[LevelLight] 错误: 设置红石灯状态时出错")
                    e.printStackTrace()
                }
            }
        }

        // 播放拉杆音效
        block.world.playSound(block.location, Sound.BLOCK_LEVER_CLICK, 1f, 1f)
    }
}