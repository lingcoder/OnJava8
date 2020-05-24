[TOC]

<!-- Concurrent Programming -->
# 第二十四章 并发编程

>爱丽丝：“但是我不想进入疯狂的人群中”
>
>猫咪：“oh，你无能为力，我们都疯了，我疯了，你也疯了”
>
>爱丽丝：“你怎么知道我疯了”。
>
>猫咪：“你一定疯了，否则你不会来到这里”——爱丽丝梦游仙境 第6章。

到目前为止，我们一直在编程，就像文学中的意识流叙事设备一样：首先发生一件事，然后是下一件事。我们完全控制所有步骤及其发生的顺序。如果我们将值设置为5，那么稍后会回来并发现它是47，这将是非常令人惊讶的。

我们现在进入了一个奇怪的并发世界，在此这个结果并不令人惊讶。你信赖的一切都不再可靠。它可能有效，也可能无效。很可能它只会在某些条件下有效。你必须知道和了解这些情况以确定哪些有效。

作为类比，你的正常生活是在牛顿力学中发生的。物体具有质量：它们会下降并转移它们的动量。电线具有阻力，光线可以直线传播。但是，如果你进入非常小、热、冷、或者大的世界（我们不能生存），这些事情会发生变化。我们无法判断某物体是粒子还是波，光是否受到重力影响，一些物质变为超导体。

假设我们在同时多条故事线进行的间谍小说里，而非单一意识流地叙事。第一个间谍在特殊的岩石处留下了微缩胶片。当第二个间谍过来准备取回包裹时，胶片可能已被第三个间谍带走了。但是小说并没有交代此处的细节。直到故事结尾，我们都没搞清楚这里到底发生了什么。

构建并发应用程序非常类似于游戏 [Jenga](https://en.wikipedia.org/wiki/Jenga)，每当你拉出一个块并将其放置在塔上时，一切都会崩溃。每个塔楼和每个应用程序都是独一无二的，有自己的作用。你从构建系统中学到的东西可能不适用于下一个系统。

本章是对并发性的一个非常基本的介绍。虽然我使用了最现代的 Java 8 工具来演示原理，但这一章远非对该主题的全面处理。我的目标是为你提供足够的基础知识，使你能够把握问题的复杂性和危险性，从而安全的通过这些鲨鱼肆虐的困难水域。

对于更麻烦和底层的细节，请参阅附录：[并发底层原理](./Appendix-Low-Level-Concurrency.md)。要进一步深入这个领域，你还必须阅读 *Brian Goetz* 等人的 《Java Concurrency in Practice》。尽管在撰写本文时，该书已有十多年的历史了，但它仍然包含我们必须了解和理解的要点。理想情况下，本章和上述附录是阅读该书的良好前提。另外，*Bill Venner* 的 《Inside the Java Virtual Machine》也是很有价值的资源。它详细描述了 JVM 的内部工作方式，包括线程。


<!-- The Terminology Problem -->

## 术语问题

在编程文献中并发、并行、多任务、多处理、多线程、分布式系统（以及可能的其他）使用了许多相互冲突的方式，并且经常被混淆。Brian Goetz在2016年的演讲中指出了这一点[From Concurrent to Parallel](https://www.youtube.com/watch?v=NsDE7E8sIdQ)，他提出了一个合理的解释：

- 并发是关于正确有效地控制对共享资源的访问。
- 并行是使用额外的资源来更快地产生结果。

这些都是很好的定义，但有几十年的混乱产生了反对解决问题的历史。一般来说，当人们使用“并发”这个词时，他们的意思是“一切变得混乱”，事实上，我可能会在很多地方自己陷入这种想法，大多数书籍，包括Brian Goetz的Java Concurrency in Practice，都在标题中使用这个词。

并发通常意味着“不止一个任务正在执行中”，而并行性几乎总是意味着“不止一个任务同时执行。”你可以立即看到这些定义的区别：并行也有不止一个任务“正在进行”。区别在于细节，究竟是如何“执行”发生的。此外，重叠：为并行编写的程序有时可以在单个处理器上运行，而一些并发编程系统可以利用多个处理器。

这是另一种方法，在减速[原文：slowdown]发生的地方写下定义：

_并发_

同时完成多个任务。在开始处理其他任务之前，当前任务不需要完成。并发解决了阻塞发生的问题。当任务无法进一步执行，直到外部环境发生变化时才会继续执行。最常见的例子是I/O，其中任务必须等待一些input（在这种情况下会被阻止）。这个问题产生在I/O密集型。

_并行_

同时在多个地方完成多个任务。这解决了所谓的计算密集型问题，如果将程序分成多个部分并在不同的处理器上编辑不同的部分，程序可以运行得更快。

术语混淆的原因在上面的定义中显示：其中核心是“在同一时间完成多个任务。”并行性通过多个处理器增加分布。更重要的是，两者解决了不同类型的问题：解决I/O密集型问题，并行化可能对你没有任何好处，因为问题不是整体速度，而是阻塞。并且考虑到计算力限制问题并试图在单个处理器上使用并发来解决它可能会浪费时间。两种方法都试图在更短的时间内完成更多，但它们实现加速的方式是不同的，并且取决于问题所带来的约束。

这两个概念混合在一起的一个主要原因是包括Java在内的许多编程语言使用相同的机制**线程**来实现并发和并行。

我们甚至可以尝试添加细致的粒度去定义（但是，这不是标准化的术语）：

- **纯并发**：任务仍然在单个CPU上运行。纯并发系统产生的结果比顺序系统更快，但如果有更多的处理器，则运行速度不会更快
- **并发-并行**：使用并发技术，结果程序利用更多处理器并更快地生成结果
- **并行-并发**：使用并行编程技术编写，如果只有一个处理器，结果程序仍然可以运行（Java 8 **Streams**就是一个很好的例子）。
- **纯并行**：除非有多个处理器，否则不会运行。

在某些情况下，这可能是一个有用的分类法。

对并发性的语言和库支持似乎[Leaky Abstraction](https://en.wikipedia.org/wiki/Leaky_abstraction)是完美候选者。抽象的目标是“抽象出”那些对于手头想法不重要的东西，从不必要的细节中汲取灵感。如果抽象是漏洞，那些碎片和细节会不断重新声明自己是重要的，无论你试图隐藏它们多少

我开始怀疑是否真的有高度抽象。当编写这些类型的程序时，你永远不会被底层系统和工具屏蔽，甚至关于CPU缓存如何工作的细节。最后，如果你非常小心，你创作的东西在特定的情况下起作用，但它在其他情况下不起作用。有时，区别在于两台机器的配置方式，或者程序的估计负载。这不是Java特有的-它是并发和并行编程的本质。

你可能会认为[纯函数式](https://en.wikipedia.org/wiki/Purely_functional)语言没有这些限制。实际上，纯函数式语言解决了大量并发问题，所以如果你正在解决一个困难的并发问题，你可以考虑用纯函数语言编写这个部分。但最终，如果你编写一个使用队列的系统，例如，如果它没有正确调整并且输入速率要么没有被正确估计或被限制（并且限制意味着,在不同情况下不同的东西具有不同的影响），该队列将填满并阻塞或溢出。最后，你必须了解所有细节，任何问题都可能会破坏你的系统。这是一种非常不同的编程方式

<!-- A New Definition ofConcurrencyFor -->
### 并发的新定义

几十年来，我一直在努力解决各种形式的并发问题，其中一个最大的挑战一直是简单地定义它。在撰写本章的过程中，我终于有了这样的洞察力，我认为可以定义它：
>**并发性是一系列性能技术，专注于减少等待**

这实际上是一个相当多的声明，所以我将其分解：

- 这是一个集合：有许多不同的方法来解决这个问题。这是使定义并发性如此具有挑战性的问题之一，因为技术差别很大
- 这些是性能技术：就是这样。并发的关键点在于让你的程序运行得更快。在Java中，并发是非常棘手和困难的，所以绝对不要使用它，除非你有一个重大的性能问题 - 即使这样，使用最简单的方法产生你需要的性能，因为并发很快变得无法管理。
- “减少等待”部分很重要而且微妙。无论（例如）你运行多少个处理器，你只能在等待某个地方时产生结果。如果你发起I/O请求并立即获得结果，没有延迟，因此无需改进。如果你在多个处理器上运行多个任务，并且每个处理器都以满容量运行，并且任何其他任务都没有等待，那么尝试提高吞吐量是没有意义的。并发的唯一形式是如果程序的某些部分被迫等待。等待可以以多种形式出现 - 这解释了为什么存在如此不同的并发方法。

值得强调的是，这个定义的有效性取决于等待这个词。如果没有什么可以等待，那就没有机会了。如果有什么东西在等待，那么就会有很多方法可以加快速度，这取决于多种因素，包括系统运行的配置，你要解决的问题类型以及其他许多问题。
<!-- Concurrency Superpowers -->
## 并发的超能力

想象一下，你置身于一部科幻电影。你必须在高层建筑中搜索一个精心巧妙地隐藏在建筑物的一千万个房间之一中的单个物品。你进入建筑物并沿着走廊向下移动。走廊分开了。

你自己完成这项任务需要一百个生命周期。

现在假设你有一个奇怪的超能力。你可以将自己一分为二，然后在继续前进的同时将另一半送到另一个走廊。每当你在走廊或楼梯上遇到分隔到下一层时，你都会重复这个分裂的技巧。最终，整个建筑中的每个走廊的终点都有一个你。

每个走廊都有一千个房间。你的超能力变得有点弱，所以你只能分裂出50个自己来搜索这间房间。

一旦克隆体进入房间，它必须搜索房间的每个角落。这时它切换到了第二种超能力。它分裂成了一百万个纳米机器人，每个机器人都会飞到或爬到房间里一些看不见的地方。你不需要了解这种功能 - 一旦你开启它就会自动工作。在他们自己的控制下，纳米机器人开始行动，搜索房间然后回来重新组装成你，突然间，你获得了寻找的物品是否在房间内的消息。

我很想说，“并发就是刚才描述的置身于科幻电影中的超能力“就像你自己可以一分为二然后解决更多的问题一样简单。但是问题在于，我们来描述这种现象的任何模型最终都是泄漏抽象的（leaky abstraction)。

以下是其中一个漏洞：在理想的世界中，每次克隆自己时，你还会复制硬件处理器来运行该克隆。但当然不会发生这种情况 - 你的机器上可能有四个或八个处理器（通常在写入时）。你可能还有更多，并且仍有许多情况只有一个处理器。在抽象的讨论中，物理处理器的分配方式不仅可以泄漏，甚至可以支配你的决策

让我们在科幻电影中改变一些东西。现在当每个克隆搜索者最终到达一扇门时，他们必须敲门并等到有人回答。如果我们每个搜索者有一个处理器，这没有问题 - 处理器只是空闲，直到门被回答。但是如果我们只有8个处理器和数千个搜索者，那么只是因为搜索者恰好是因为处理器闲置了被锁，等待一扇门被接听。相反，我们希望将处理器应用于搜索，在那里它可以做一些真正的工作，因此需要将处理器从一个任务切换到另一个任务的机制。

许多型号能够有效地隐藏处理器的数量，并允许你假装你的数量非常大。但是有些情况会发生故障的时候，你必须知道处理器的数量，以便你可以解决这个问题。

其中一个最大的影响取决于你是单个处理器还是多个处理器。如果你只有一个处理器，那么任务切换的成本也由该处理器承担，将并发技术应用于你的系统会使它运行得更慢。

这可能会让你决定，在单个处理器的情况下，编写并发代码时没有意义。然而，有些情况下，并发模型会产生更简单的代码，实际上值得让它运行得更慢以实现。

在克隆体敲门等待的情况下，即使单处理器系统也能从并发中受益，因为它可以从等待（阻塞）的任务切换到准备好的任务。但是如果所有任务都可以一直运行那么切换的成本会降低一切，在这种情况下，如果你有多个进程，并发通常只会有意义。

在接听电话的客户服务部门，你只有一定数量的人，但是你可以拨打很多电话。那些人（处理器）必须一次拨打一个电话，直到完成电话和额外的电话必须排队。

在“鞋匠和精灵”的童话故事中，鞋匠做了很多工作，当他睡着时，一群精灵来为他制作鞋子。这里的工作是分布式的，但即使使用大量的物理处理器，在制造鞋子的某些部件时会产生限制 - 例如，如果鞋底需要制作鞋子，这会限制制鞋的速度并改变你设计解决方案的方式。

因此，你尝试解决的问题驱动解决方案的设计。打破一个“独立运行”问题的高级[原文：lovely ]抽象，然后就是实际发生的现实。物理现实不断侵入和震撼，这种抽象。

这只是问题的一部分。考虑一个制作蛋糕的工厂。我们不知何故在工人中分发了蛋糕制作任务，但是现在是时候让工人把蛋糕放在盒子里了。那里有一个盒子，准备收到蛋糕。但是，在工人将蛋糕放入盒子之前，另一名工人投入并将蛋糕放入盒子中！我们的工人已经把蛋糕放进去了，然后就开始了！这两个蛋糕被砸碎并毁了。这是常见的“共享内存”问题，产生我们称之为竞争条件的问题，其结果取决于哪个工作人员可以首先在框中获取蛋糕（通常使用锁定机制来解决问题，因此一个工作人员可以先抓住框并防止蛋糕砸）。

当“同时”执行的任务相互干扰时，会出现问题。他可以以如此微妙和偶然的方式发生，可能公平地说，并发性“可以说是确定性的，但实际上是非确定性的。”也就是说，你可以假设编写通过维护和代码检查正常工作的并发程序。然而，在实践中，编写仅看起来可行的并发程序更为常见，但是在适当的条件下，将会失败。这些情况可能会发生，或者很少发生，你在测试期间从未看到它们。实际上，编写测试代码通常无法为并发程序生成故障条件。由此产生的失败只会偶尔发生，因此它们以客户投诉的形式出现。
这是推动并发的最强有力的论据之一：如果你忽略它，你可能会被咬。

因此，并发似乎充满了危险，如果这让你有点害怕，这可能是一件好事。尽管Java 8在并发性方面做出了很大改进，但仍然没有像编译时验证(compile-time verification)或受检查的异常(checked exceptions)那样的安全网来告诉你何时出现错误。通过并发，你只能依靠自己，只有知识渊博，保持怀疑和积极进取的人，才能用Java编写可靠的并发代码。

<!-- Concurrency is for Speed -->
<!-- 不知道是否可以找到之前翻译的针对速度感觉太直了 -->
## 并发为速度而生

在听说并发编程的问题之后，你可能会想知道它是否值得这么麻烦。答案是“不，除非你的程序运行速度不够快。”并且在决定它没有之前你会想要仔细思考。不要随便跳进并发编程的悲痛之中。如果有一种方法可以在更快的机器上运行你的程序，或者如果你可以对其进行分析并发现瓶颈并在该位置交换更快的算法，那么请执行此操作。只有在显然没有其他选择时才开始使用并发，然后仅在孤立的地方。

速度问题一开始听起来很简单：如果你想要一个程序运行得更快，将其分解成碎片并在一个单独的处理器上运行每个部分。由于我们能够提高时钟速度流（至少对于传统芯片），速度的提高是出现在多核处理器的形式而不是更快的芯片。为了使你的程序运行得更快，你必须学习利用那些超级处理器，这是并发性给你的一个建议。

使用多处理器机器，可以在这些处理器之间分配多个任务，这可以显着提高吞吐量。强大的多处理器Web服务器通常就是这种情况，它可以在程序中为CPU分配大量用户请求，每个请求分配一个线程。

但是，并发性通常可以提高在单个处理器上运行的程序的性能。这听起来有点违反直觉。如果考虑一下，由于上下文切换的成本增加（从一个任务更改为另一个任务），在单个处理器上运行的并发程序实际上应该比程序的所有部分顺序运行具有更多的开销。在表面上，将程序的所有部分作为单个任务运行并节省上下文切换的成本似乎更便宜。

可以产生影响的问题是阻塞。如果你的程序中的一个任务由于程序控制之外的某些条件（通常是I/O）而无法继续，我们会说任务或线程阻塞（在我们的科幻故事中，克隆体已敲门而且是等待它打开）。如果没有并发性，整个程序就会停止，直到外部条件发生变化。但是，如果使用并发编写程序，则当一个任务被阻止时，程序中的其他任务可以继续执行，因此程序继续向前移动。实际上，从性能的角度来看，在单处理器机器上使用并发是没有意义的，除非其中一个任务可能阻塞。

单处理器系统中性能改进的一个常见例子是事件驱动编程，特别是用户界面编程。考虑一个程序执行一些长时间运行操作，从而最终忽略用户输入和无响应。如果你有一个“退出”按钮，你不想在你编写的每段代码中轮询它。这会产生笨拙的代码，无法保证程序员不会忘记执行检查。没有并发性，生成响应式用户界面的唯一方法是让所有任务定期检查用户输入。通过创建单独的执行线程来响应用户输入，该程序保证了一定程度的响应。

实现并发的直接方法是在操作系统级别，使用与线程不同的进程。进程是一个在自己的地址空间内运行的自包含程序。进程很有吸引力，因为操作系统通常将一个进程与另一个进程隔离，因此它们不会相互干扰，这使得进程编程相对容易。相比之下，线程共享内存和I/O等资源，因此编写多线程程序时遇到的困难是在不同的线程驱动的任务之间协调这些资源，一次不能通过多个任务访问它们。
<!-- 文献引用未加，因为暂时没看到很好的解决办法 -->
有些人甚至提倡将进程作为并发的唯一合理方法[^1]，但不幸的是，通常存在数量和开销限制，以防止它们在并发频谱中的适用性（最终你习惯了标准的并发性克制，“这种方法适用于一些情况但不适用于其他情况”）

一些编程语言旨在将并发任务彼此隔离。这些通常被称为_函数式语言_，其中每个函数调用不产生其他影响（因此不能与其他函数干涉），因此可以作为独立的任务来驱动。Erlang就是这样一种语言，它包括一个任务与另一个任务进行通信的安全机制。如果你发现程序的一部分必须大量使用并发性并且你在尝试构建该部分时遇到了过多的问题，那么你可能会考虑使用专用并发语言创建程序的那一部分。
<!-- 文献标记 -->
Java采用了更传统的方法[^2]，即在顺序语言之上添加对线程的支持而不是在多任务操作系统中分配外部进程，线程在执行程序所代表的单个进程中创建任务交换。

并发性会带来成本，包括复杂性成本，但可以通过程序设计，资源平衡和用户便利性的改进来抵消。通常，并发性使你能够创建更加松散耦合的设计;否则，你的代码部分将被迫明确标注通常由并发处理的操作。

<!-- The Four Maxims of Java Concurrency -->
## 四句格言

在经历了多年的Java并发之后，我总结了以下四个格言：
>1.不要这样做
>
>2.没有什么是真的，一切可能都有问题
>
>3.它起作用,并不意味着它没有问题
>
>4.你仍然必须理解它

这些特别是关于Java设计中的问题，尽管它也可以应用于其他一些语言。但是，确实存在旨在防止这些问题的语言。

### 1.不要这样做

（不要自己动手）

避免纠缠于并发产生的深层问题的最简单方法就是不要这样做。虽然它是诱人的，并且似乎足够安全，可以尝试做简单的事情，但它存在无数、微妙的陷阱。如果你可以避免它，你的生活会更容易。

证明并发性的唯一因素是速度。如果你的程序运行速度不够快 - 在这里要小心，因为只是希望它运行得更快是不合理的 - 首先应用一个分析器（参见代码校验章中分析和优化）来发现你是否可以执行其他一些优化。

如果你被迫进行并发，请采取最简单，最安全的方法来解决问题。使用众所周知的库并尽可能少地编写自己的代码。有了并发性，就没有“太简单了”。自负才是你的敌人。

### 2.没有什么是真的，一切可能都有问题

没有并发性的编程，你会发现你的世界有一定的顺序和一致性。通过简单地将变量赋值给某个值，很明显它应该始终正常工作。

在并发领域，有些事情可能是真的而有些事情却不是，你必须认为没有什么是真的。你必须质疑一切。即使将变量设置为某个值也可能或者可能不会按预期的方式工作，并且从那里开始走下坡路。我已经很熟悉的东西，认为它显然有效但实际上并没有。

在非并发程序中你可以忽略的各种事情突然变得非常重要。例如，你必须知道处理器缓存以及保持本地缓存与主内存一致的问题。你必须了解对象构造的深度复杂性，以便你的构造对象不会意外地将数据暴露给其他线程进行更改。问题还有很多。

虽然这些主题太复杂，无法为你提供本章的专业知识（再次参见Java Concurrency in Practice），但你必须了解它们。

### 3.它起作用,并不意味着它没有问题

我们很容易编写出一个看似完美实则有问题的并发程序，并且往往问题直在极端情况下才暴露出来 - 在程序部署后不可避免地会出现用户问题。

- 你不能证明并发程序是正确的，你只能（有时）证明它是不正确的。
- 大多数情况下你甚至不能这样做：如果它有问题，你可能无法检测到它。
- 你通常不能编写有用的测试，因此你必须依靠代码检查结合深入的并发知识来发现错误。
- 即使是有效的程序也只能在其设计参数下工作。当超出这些设计参数时，大多数并发程序会以某种方式失败。

在其他 Java 主题中，我们培养了一种感觉-决定论。一切都按照语言的承诺（或隐含）进行，这是令人欣慰和期待的 - 毕竟，编程语言的目的是让机器做我们想要的。从确定性编程的世界进入并发编程领域，我们遇到了一种称为[Dunning-Kruger](https://en.wikipedia.org/wiki/Dunning%E2%80%93Kruger_effect)效应的认知偏差，可以概括为“无知者无畏。”这意味着“......相对不熟练的人拥有着虚幻的优越感，错误地评估他们的能力远高于实际。

我自己的经验是，无论你是多么确定你的代码是线程安全的，它可能已经无效了。你可以很容易地了解所有的问题，然后几个月或几年后你会发现一些概念让你意识到你编写的大多数内容实际上都容易受到并发错误的影响。当某些内容不正确时，编译器不会告诉你。为了使它正确，你必须在研究代码时掌握前脑的所有并发问题。

在Java的所有非并发领域，“没有明显的错误和没有明显的编译错误”似乎意味着一切都好。对于并发，它没有任何意义。你可以在这个情况下做的最糟糕的事情是“自信”。

### 4.你必须仍然理解

在格言1-3之后，你可能会对并发性感到害怕，并且认为，“到目前为止，我已经避免了它，也许我可以继续保留它。

这是一种理性的反应。你可能知道其他编程语言更好地设计用于构建并发程序 - 甚至是在JVM上运行的程序（从而提供与Java的轻松通信），例如Clojure或Scala。为什么不用这些语言编写并发部分并将Java用于其他所有部分呢？

唉，你不能轻易逃脱：

- 即使你从未明确地创建一个线程，你可能使用的框架 - 例如，Swing图形用户界面（GUI）库，或者像**Timer** clas那样简单的东西。
- 这是最糟糕的事情：当你创建组件时，你必须假设这些组件可能在多线程环境中重用。即使你的解决方案是放弃并声明你的组件“不是线程安全的”，你仍然必须知道这样的声明是重要的，它是什么意思？

人们有时会认为并发性太难，不能包含在介绍该语言的书中。他们认为并发是一个可以独立对待的独立主题，并且它在日常编程中出现的少数情况（例如图形用户界面）可以用特殊的习语来处理。如果你可以避免它，为什么要介绍这样的复杂的主题。

唉，如果只是这样的话，那就太好了。但不幸的是，你无法选择何时在Java程序中出现线程。仅仅你从未写过自己的线程，并不意味着你可以避免编写线程代码。例如，Web系统是最常见的Java应用程序之一，本质上是多线程的Web服务器通常包含多个处理器，而并行性是利用这些处理器的理想方式。就像这样的系统看起来那么简单，你必须理解并发才能正确地编写它。

Java是一种多线程语言，如果你了解它们是否存在并发问题。因此，有许多Java程序正在使用中，或者只是偶然工作，或者大部分时间工作并且不时地发生问题，因为。有时这种问题是相对良性的，但有时它意味着丢失有价值的数据，如果你没有意识到并发问题，你最终可能会把问题放在其他地方而不是你的代码中。如果将程序移动到多处理器系统，则可以暴露或放大这些类型的问题。基本上，了解并发性使你意识到正确的程序可能会表现出错误的行为。

<!-- The Brutal Truth -->
## <span id = "The-Brutal-Truth">残酷的真相</span>

当人类开始烹饪他们的食物时，他们大大减少了他们的身体分解和消化食物所需的能量。烹饪创造了一个“外化的胃”，从而释放出追去其他的的能力。火的使用促成了文明。

我们现在通过计算机和网络技术创造了一个“外化大脑”，开始了第二次基本转变。虽然我们只是触及表面，但已经引发了其他转变，例如设计生物机制的能力，并且已经看到文化演变的显着加速（过去，人们不得不前往混合文化，但现在他们开始混合互联网）。这些转变的影响和好处已经超出了科幻作家预测它们的能力（他们在预测文化和个人变化，甚至技术转变的次要影响方面都特别困难）。

有了这种根本性的人类变化，看到许多破坏和失败的实验并不令人惊讶。实际上，进化依赖于无数的实验，其中大多数都失败了。这些实验是向前发展的必要条件

Java是在充满自信，热情和睿智的氛围中创建的。在发明一种编程语言时，很容易就像语言的初始可塑性会持续存在一样，你可以把某些东西拿出来，如果不能解决问题，那么就修复它。编程语言以这种方式是独一无二的 - 它们经历了类似水的改变：气态，液态和最终的固态。在气体相位期间，灵活性似乎是无限的，并且很容易认为它总是那样。一旦人们开始使用你的语言，变化就会变得更加严重，环境变得更加粘稠。语言设计的过程本身就是一门艺术。

紧迫感来自互联网的最初兴起。它似乎是一场比赛，第一个通过起跑线的人将“获胜”（事实上，Java，JavaScript和PHP等语言的流行程度可以证明这一点）。唉，通过匆忙设计语言而产生的认知负荷和技术债务最终会赶上我们。

[Turing completeness](https://en.wikipedia.org/wiki/Turing_completeness)是不足够的;语言需要更多的东西：它们必须能够创造性地表达，而不是用不必要的东西来衡量我们。解放我们的心理能力只是为了扭转并再次陷入困境，这是毫无意义的。我承认，尽管存在这些问题，我们已经完成了令人惊奇的事情，但我也知道如果没有这些问题我们能做得更多。

热情使原始Java设计师因为看起来有必要而投入功能。信心（以及原始语言的气味）让他们认为任何问题都可以解决。在时间轴的某个地方，有人认为任何加入Java的东西是固定的和永久性的 - 这是非常有信心，相信第一个决定永远是正确的，因此我们看到Java的体系中充斥着糟糕的决策。其中一些决定最终没有什么后果;例如，你可以告诉人们不要使用Vector，但保留了对之前版本的支持。

线程包含在Java 1.0中。当然，并发性是影响语言远角的基本语言设计决策，很难想象以后添加它。公平地说，当时并不清楚基本的并发性是多少。像C这样的其他语言能够将线程视为一个附加功能，因此Java设计师也纷纷效仿，包括一个Thread类和必要的JVM支持（这比你想象的要复杂得多）。

C语言是面向过程语言，这限制了它的野心。这些限制使附加线程库合理。当采用原始模型并将其粘贴到复杂语言中时，Java的大规模扩展迅速暴露了基本问题。在Thread类中的许多方法的弃用以及后续的高级库浪潮中，这种情况变得明显，这些库试图提供更好的并发抽象。

不幸的是，为了在更高级别的语言中获得并发性，所有语言功能都会受到影响，包括最基本的功能，例如标识符代表可变值。在函数和方法中，所有不变和防止副作用的方法都会导致简化并发编程（这些是纯函数式编程语言的基础）的变化，但当时对于主流语言的创建者来说似乎是奇怪的想法。最初的Java设计师要么对这些选择有所了解，要么认为它们太不同了，并且会抛弃许多潜在的语言采用者。我们可以慷慨地说，语言设计社区当时根本没有足够的经验来理解调整在线程库中的影响。

Java实验告诉我们，结果是悄然灾难性的。程序员很容易陷入认为Java 线程并不那么困难的陷阱。似乎工作的程序充满了微妙的并发bug。

为了获得正确的并发性，语言功能必须从头开始设计并考虑并发性。这艘船航行了;Java将不再是为并发而设计的语言，而只是一种允许它的语言。

尽管有这些基本的不可修复的缺陷，但令人印象深刻的是它还有多远。Java的后续版本添加了库，以便在使用并发时提升抽象级别。事实上，我根本不会想到有可能在Java 8中进行改进：并行流和**CompletableFutures** - 这是惊人的史诗般的变化，我会惊奇地重复的查看它[^3]。

这些改进非常有用，我们将在本章重点介绍并行流和**CompletableFutures**。虽然它们可以大大简化你对并发和后续代码的思考方式，但基本问题仍然存在：由于Java的原始设计，代码的所有部分仍然容易受到攻击，你仍然必须理解这些复杂和微妙的问题。Java中的线程绝不是简单或安全的;那种经历必须降级为另一种更新的语言。

<!-- The Rest of the Chapter -->
## 本章其余部分

这是我们将在本章的其余部分介绍的内容。请记住，本章的重点是使用最新的高级Java并发结构。使用这些使得你的生活比旧的替代品更加轻松。但是，你仍会在遗留代码中遇到一些低级工具。有时，你可能会被迫自己使用其中的一些。附录：[并发底层原理](./Appendix-Low-Level-Concurrency.md)包含一些更原始的Java并发元素的介绍。

- Parallel Streams（并行流）
到目前为止，我已经强调了Java 8 Streams提供的改进语法。现在你对该语法（作为一个粉丝，我希望）感到满意，你可以获得额外的好处：你可以通过简单地将parallel()添加到表达式来并行化流。这是一种简单，强大，坦率地说是利用多处理器的惊人方式

添加parallel()来提高速度似乎是微不足道的，但是，唉，它就像你刚刚在[残酷的真相](#The-Brutal-Truth)中学到的那样简单。我将演示并解释一些盲目添加parallel()到Stream表达式的缺陷。

- 创建和运行任务
任务是一段可以独立运行的代码。为了解释创建和运行任务的一些基础知识，本节介绍了一种比并行流或CompletableFutures：Executor更复杂的机制。执行者管理一些低级Thread对象（Java中最原始的并发形式）。你创建一个任务，然后将其交给Executorto运行。

有多种类型的Executor用于不同的目的。在这里，我们将展示规范形式，代表创建和运行任务的最简单和最佳方法。

- 终止长时间运行的任务
任务独立运行，因此需要一种机制来关闭它们。典型的方法使用了一个标志，这引入了共享内存的问题，我们将使用Java的“Atomic”库来回避它。
- Completable Futures
当你将衣服带到干洗店时，他们会给你一张收据。你继续完成其他任务，最终你的衣服很干净，你可以拿起它。收据是你与干洗店在后台执行的任务的连接。这是Java 5中引入的Future的方法。

Future比以前的方法更方便，但你仍然必须出现并用收据取出干洗，等待任务没有完成。对于一系列操作，Futures并没有真正帮助那么多。

Java 8 CompletableFuture是一个更好的解决方案：它允许你将操作链接在一起，因此你不必将代码写入接口排序操作。有了CompletableFuture完美的结合，就可以更容易地做出“采购原料，组合成分，烹饪食物，提供食物，清理菜肴，储存菜肴”等一系列链式操作。

- 死锁
某些任务必须去**等待 - 阻塞**来获得其他任务的结果。被阻止的任务有可能等待另一个被阻止的任务，等待另一个被阻止的任务，等等。如果被阻止的任务链循环到第一个，没有人可以取得任何进展，你就会陷入僵局。

如果在运行程序时没有立即出现死锁，则会出现最大的问题。你的系统可能容易出现死锁，并且只会在某些条件下死锁。程序可能在某个平台上运行正常，例如你的开发机器，但是当你将其部署到不同的硬件时会开始死锁。

死锁通常源于细微的编程错误;一系列无辜的决定，最终意外地创建了一个依赖循环。本节包含一个经典示例，演示了死锁的特性。

我们将通过模拟创建披萨的过程完成本章，首先使用并行流实现它，然后是完成配置。这不仅仅是两种方法的比较，更重要的是探索你应该投入多少工作来加速计划。

- 努力，复杂，成本
<!-- Parallel Streams -->
## 并行流

Java 8流的一个显着优点是，在某些情况下，它们可以很容易地并行化。这来自仔细的库设计，特别是流使用内部迭代的方式 - 也就是说，它们控制着自己的迭代器。特别是，他们使用一种特殊的迭代器，称为Spliterator，它被限制为易于自动分割。这产生了相当神奇的结果，即能够简单用parallel()然后流中的所有内容都作为一组并行任务运行。如果你的代码是使用Streams编写的，那么并行化以提高速度似乎是一种琐事

例如，考虑来自Streams的Prime.java。查找质数可能是一个耗时的过程，我们可以看到该程序的计时：

```java
// concurrent/ParallelPrime.java
import java.util.*;
import java.util.stream.*;
import static java.util.stream.LongStream.*;
import java.io.*;
import java.nio.file.*;
import onjava.Timer;

public class ParallelPrime {
    static final int COUNT = 100_000;
    public static boolean isPrime(long n){
        return rangeClosed(2, (long)Math.sqrt(n)).noneMatch(i -> n % i == 0);
        }
    public static void main(String[] args)
        throws IOException {
        Timer timer = new Timer();
        List<String> primes =
            iterate(2, i -> i + 1)
                .parallel()              // [1]
                .filter(ParallelPrime::isPrime)
                .limit(COUNT)
                .mapToObj(Long::toString)
                .collect(Collectors.toList());
        System.out.println(timer.duration());
        Files.write(Paths.get("primes.txt"), primes, StandardOpenOption.CREATE);
        }
    }
```

输出结果：

```
    Output:
    1224
```

请注意，这不是微基准测试，因为我们计时整个程序。我们将数据保存在磁盘上以防止过激的优化;如果我们没有对结果做任何事情，那么一个高级的编译器可能会观察到程序没有意义并且消除了计算（这不太可能，但并非不可能）。请注意使用nio2库编写文件的简单性（在[文件](./17-Files.md)一章中有描述）。

当我注释掉[1] parallel()行时，我的结果大约是parallel()的三倍。

并行流似乎是一个甜蜜的交易。你所需要做的就是将编程问题转换为流，然后插入parallel()以加快速度。实际上，有时候这很容易。但遗憾的是，有许多陷阱。

- parallel()不是灵丹妙药

作为对流和并行流的不确定性的探索，让我们看一个看似简单的问题：求和数字的增量序列。事实证明这是一个令人惊讶的数量，并且我将冒险将它们进行比较 - 试图小心，但承认我可能会在计时代码执行时遇到许多基本陷阱之一。结果可能有一些缺陷（例如JVM没有“升温”），但我认为它仍然提供了一些有用的指示。

我将从一个计时方法rigorously 开始，它采用**LongSupplier**，测量**getAsLong()**调用的长度，将结果与**checkValue**进行比较并显示结果。

请注意，一切都必须严格使用**long**;我花了一些时间发现隐蔽的溢出，然后才意识到在重要的地方错过了**long**。

所有关于时间和内存的数字和讨论都是指“我的机器”。你的经历可能会有所不同。

```java
// concurrent/Summing.java
import java.util.stream.*;
import java.util.function.*;
import onjava.Timer;
public class Summing {
    static void timeTest(String id, long checkValue,    LongSupplier operation){
        System.out.print(id + ": ");
        Timer timer = new Timer();
        long result = operation.getAsLong();
        if(result == checkValue)
            System.out.println(timer.duration() + "ms");
        else
            System.out.format("result: %d%ncheckValue: %d%n", result, checkValue);
        }
    public static final int SZ = 100_000_000;// This even works://
    public static final int SZ = 1_000_000_000;
    public static final long CHECK = (long)SZ * ((long)SZ + 1)/2; // Gauss's formula
    public static void main(String[] args){
        System.out.println(CHECK);
        timeTest("Sum Stream", CHECK, () ->
        LongStream.rangeClosed(0, SZ).sum());
        timeTest("Sum Stream Parallel", CHECK, () ->
        LongStream.rangeClosed(0, SZ).parallel().sum());
        timeTest("Sum Iterated", CHECK, () ->
        LongStream.iterate(0, i -> i + 1)
        .limit(SZ+1).sum());
        // Slower & runs out of memory above 1_000_000:
        // timeTest("Sum Iterated Parallel", CHECK, () ->
        //   LongStream.iterate(0, i -> i + 1)
        //     .parallel()
        //     .limit(SZ+1).sum());
    }
}
```

输出结果：

```
5000000050000000
Sum Stream: 167ms
Sum Stream Parallel: 46ms
Sum Iterated: 284ms
```

**CHECK**值是使用Carl Friedrich Gauss在1700年代后期仍在小学时创建的公式计算出来的.

 **main()** 的第一个版本使用直接生成 **Stream** 并调用 **sum()** 的方法。我们看到流的好处在于十亿分之一的SZ在没有溢出的情况下处理（我使用较小的数字，因此程序运行时间不长）。使用 **parallel()** 的基本范围操跟快。

如果使用**iterate()**来生成序列，则减速是戏剧性的，可能是因为每次生成数字时都必须调用lambda。但是如果我们尝试并行化，那么结果通常比非并行版本花费的时间更长，但是当**SZ**超过一百万时，它也会耗尽内存（在某些机器上）。当然，当你可以使用**range()**时，你不会使用**iterate()**，但如果你生成的东西不是简单的序列，你必须使用**iterate()**。应用**parallel()**是一个合理的尝试，但会产生令人惊讶的结果。我们将在后面的部分中探讨内存限制的原因，但我们可以对流并行算法进行初步观察：

- 流并行性将输入数据分成多个部分，因此算法可以应用于那些单独的部分。
- 阵列分割成本低廉，均匀且具有完美的分裂知识。
- 链接列表没有这些属性;“拆分”一个链表仅仅意味着把它分成“第一元素”和“其余列表”，这相对无用。
- 无状态生成器的行为类似于数组;使用上述范围是无可争议的。
- 迭代生成器的行为类似于链表; **iterate()** 是一个迭代生成器。

现在让我们尝试通过在数组中填充值来填充数组来解决问题。因为数组只分配了一次，所以我们不太可能遇到垃圾收集时序问题。

首先我们将尝试一个充满原始**long**的数组：

```java
// concurrent/Summing2.java
// {ExcludeFromTravisCI}import java.util.*;
public class Summing2 {
    static long basicSum(long[] ia) {
        long sum = 0;
        int size = ia.length;
        for(int i = 0; i < size; i++)
            sum += ia[i];return sum;
    }
    // Approximate largest value of SZ before
    // running out of memory on mymachine:
    public static final int SZ = 20_000_000;
    public static final long CHECK = (long)SZ * ((long)SZ + 1)/2;
    public static void main(String[] args) {
        System.out.println(CHECK);
        long[] la = newlong[SZ+1];
        Arrays.parallelSetAll(la, i -> i);
        Summing.timeTest("Array Stream Sum", CHECK, () ->
        Arrays.stream(la).sum());
        Summing.timeTest("Parallel", CHECK, () ->
        Arrays.stream(la).parallel().sum());
        Summing.timeTest("Basic Sum", CHECK, () ->
        basicSum(la));// Destructive summation:
        Summing.timeTest("parallelPrefix", CHECK, () -> {
            Arrays.parallelPrefix(la, Long::sum);
        return la[la.length - 1];
        });
    }
}
```

输出结果：

```
200000010000000
Array Stream
Sum: 104ms
Parallel: 81ms
Basic Sum: 106ms
parallelPrefix: 265ms
```

第一个限制是内存大小;因为数组是预先分配的，所以我们不能创建几乎与以前版本一样大的任何东西。并行化可以加快速度，甚至比使用 **basicSum()** 循环更快。有趣的是， **Arrays.parallelPrefix()** 似乎实际上减慢了速度。但是，这些技术中的任何一种在其他条件下都可能更有用 - 这就是为什么你不能做出任何确定性的声明，除了“你必须尝试一下”。”

最后，考虑使用盒装**Long**的效果:

```java
// concurrent/Summing3.java
// {ExcludeFromTravisCI}
import java.util.*;
public class Summing3 {
    static long basicSum(Long[] ia) {
        long sum = 0;
        int size = ia.length;
        for(int i = 0; i < size; i++)
            sum += ia[i];
            return sum;
    }
    // Approximate largest value of SZ before
    // running out of memory on my machine:
    public static final int SZ = 10_000_000;
    public static final long CHECK = (long)SZ * ((long)SZ + 1)/2;
    public static void main(String[] args) {
        System.out.println(CHECK);
        Long[] aL = newLong[SZ+1];
        Arrays.parallelSetAll(aL, i -> (long)i);
        Summing.timeTest("Long Array Stream Reduce", CHECK, () ->
        Arrays.stream(aL).reduce(0L, Long::sum));
        Summing.timeTest("Long Basic Sum", CHECK, () ->
        basicSum(aL));
        // Destructive summation:
        Summing.timeTest("Long parallelPrefix",CHECK, ()-> {
            Arrays.parallelPrefix(aL, Long::sum);
            return aL[aL.length - 1];
            });
    }
}
```

输出结果：

```
50000005000000
Long Array
Stream Reduce: 1038ms
Long Basic
Sum: 21ms
Long parallelPrefix: 3616ms
```

现在可用的内存量大约减半，并且所有情况下所需的时间都会很长，除了**basicSum()**，它只是循环遍历数组。令人惊讶的是， **Arrays.parallelPrefix()** 比任何其他方法都要花费更长的时间。

我将 **parallel()** 版本分开了，因为在上面的程序中运行它导致了一个冗长的垃圾收集，扭曲了结果：

```java
// concurrent/Summing4.java
// {ExcludeFromTravisCI}
import java.util.*;
public class Summing4 {
    public static void main(String[] args) {
        System.out.println(Summing3.CHECK);
        Long[] aL = newLong[Summing3.SZ+1];
        Arrays.parallelSetAll(aL, i -> (long)i);
        Summing.timeTest("Long Parallel",
        Summing3.CHECK, () ->
        Arrays.stream(aL)
        .parallel()
        .reduce(0L,Long::sum));
    }
}
```

输出结果：

```
50000005000000
Long Parallel: 1014ms
```

它比非parallel()版本略快，但并不显着。

这种时间增加的一个重要原因是处理器内存缓存。使用**Summing2.java**中的原始**long**，数组**la**是连续的内存。处理器可以更容易地预测该阵列的使用，并使缓存充满下一个需要的阵列元素。访问缓存比访问主内存快得多。似乎 **Long parallelPrefix** 计算受到影响，因为它为每个计算读取两个数组元素，并将结果写回到数组中，并且每个都为**Long**生成一个超出缓存的引用。

使用**Summing3.java**和**Summing4.java**，**aL**是一个**Long**数组，它不是一个连续的数据数组，而是一个连续的**Long**对象引用数组。尽管该数组可能会在缓存中出现，但指向的对象几乎总是超出缓存。

这些示例使用不同的SZ值来显示内存限制。

为了进行时间比较，以下是SZ设置为最小值1000万的结果：

**Sum Stream: 69msSum
Stream Parallel: 18msSum
Iterated: 277ms
Array Stream Sum: 57ms
Parallel: 14ms
Basic Sum: 16ms
parallelPrefix: 28ms
Long Array Stream Reduce: 1046ms
Long Basic Sum: 21ms
Long parallelPrefix: 3287ms
Long Parallel: 1008ms**

虽然Java 8的各种内置“并行”工具非常棒，但我认为它们被视为神奇的灵丹妙药：“只需添加parallel()并且它会更快！”我希望我已经开始表明情况并非所有都是如此，并且盲目地应用内置的“并行”操作有时甚至会使运行速度明显变慢。

- parallel()/limit()交点

使用parallel()时会有更复杂的问题。从其他语言中吸取的流是围绕无限流模型设计的。如果你拥有有限数量的元素，则可以使用集合以及为有限大小的集合设计的关联算法。如果你使用无限流，则使用针对流优化的算法。

Java 8将两者合并起来。例如，**Collections**没有内置的**map()**操作。在Collection和Map中唯一类似流的批处理操作是**forEach()**。如果要执行**map()**和**reduce()**等操作，必须首先将Collection转换为存在这些操作的Stream:

```java
// concurrent/CollectionIntoStream.java
import onjava.*;
import java.util.*;
import java.util.stream.*;
public class CollectionIntoStream {
    public static void main(String[] args) {
    List<String> strings = Stream.generate(new Rand.String(5))
    .limit(10)
    .collect(Collectors.toList());
    strings.forEach(System.out::println);
    // Convert to a Stream for many more options:
    String result = strings.stream()
    .map(String::toUpperCase)
    .map(s -> s.substring(2))
    .reduce(":", (s1, s2) -> s1 + s2);
    System.out.println(result);
    }
}
```

输出结果：

```
pccux
szgvg
meinn
eeloz
tdvew
cippc
ygpoa
lkljl
bynxt
:PENCUXGVGINNLOZVEWPPCPOALJLNXT
```

**Collection**确实有一些批处理操作，如**removeAll()**，**removeIf()**和**retainAll()**，但这些都是破坏性的操作.**ConcurrentHashMap**对**forEachand**和**reduce**操作有特别广泛的支持。

在许多情况下，只在集合上调用**stream()**或者**parallelStream()**没有问题。但是，有时将**Stream**与**Collection**混合会产生意外。这是一个有趣的难题：

```java
// concurrent/ParallelStreamPuzzle.java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
public class ParallelStreamPuzzle {
    static class IntGenerator
    implements Supplier<Integer> {
        private int current = 0;
        public Integer get() {
            return current++;
        }
    }
    public static void main(String[] args) {
        List<Integer> x = Stream.generate(newIntGenerator())
        .limit(10)
        .parallel()  // [1]
        .collect(Collectors.toList());
        System.out.println(x);
    }
}
/* Output:
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
*/
```

如果[1]注释运行它，它会产生预期的：
**[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]**
每次。但是包含了parallel()，它看起来像一个随机数生成器，带有输出（从一次运行到下一次运行不同），如：
**[0, 3, 6, 8, 11, 14, 17, 20, 23, 26]**
这样一个简单的程序怎么会这么破碎呢？让我们考虑一下我们在这里要实现的目标：“并行生成。”“那意味着什么？一堆线程都在拉动一个生成器，在某种程度上选择一组有限的结果？代码使它看起来很简单，但它转向是一个特别凌乱的问题。

为了看到它，我们将添加一些仪器。由于我们正在处理线程，因此我们必须将任何跟踪信息捕获到并发数据结构中。在这里我使用**ConcurrentLinkedDeque**：

```java
// concurrent/ParallelStreamPuzzle2.java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.nio.file.*;
public class ParallelStreamPuzzle2 {
    public static final Deque<String> trace =
    new ConcurrentLinkedDeque<>();
    static class
    IntGenerator implements Supplier<Integer> {
        private AtomicInteger current =
        new AtomicInteger();
        public Integerget() {
            trace.add(current.get() + ": " +Thread.currentThread().getName());
            return current.getAndIncrement();
        }
    }
    public static void main(String[] args) throws Exception {
    List<Integer> x = Stream.generate(newIntGenerator())
    .limit(10)
    .parallel()
    .collect(Collectors.toList());
    System.out.println(x);
    Files.write(Paths.get("PSP2.txt"), trace);
    }
}
```

输出结果：

```
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
```

current是使用线程安全的 **AtomicInteger** 类定义的，可以防止竞争条件；**parallel()**允许多个线程调用**get()**。

在查看 **PSP2.txt**.**IntGenerator.get()** 被调用1024次时，你可能会感到惊讶。

**0: main
1: ForkJoinPool.commonPool-worker-1
2: ForkJoinPool.commonPool-worker-2
3: ForkJoinPool.commonPool-worker-2
4: ForkJoinPool.commonPool-worker-1
5: ForkJoinPool.commonPool-worker-1
6: ForkJoinPool.commonPool-worker-1
7: ForkJoinPool.commonPool-worker-1
8: ForkJoinPool.commonPool-worker-4
9: ForkJoinPool.commonPool-worker-4
10: ForkJoinPool.commonPool-worker-4
11: main
12: main
13: main
14: main
15: main...10
17: ForkJoinPool.commonPool-worker-110
18: ForkJoinPool.commonPool-worker-610
19: ForkJoinPool.commonPool-worker-610
20: ForkJoinPool.commonPool-worker-110
21: ForkJoinPool.commonPool-worker-110
22: ForkJoinPool.commonPool-worker-110
23: ForkJoinPool.commonPool-worker-1**

这个块大小似乎是内部实现的一部分（尝试使用**limit()**的不同参数来查看不同的块大小）。将**parallel()**与**limit()**结合使用可以预取一串值，作为流输出。

试着想象一下这里发生了什么：一个流抽象出无限序列，按需生成。当你要求它并行产生流时，你要求所有这些线程尽可能地调用get()。添加limit()，你说“只需要这些。”基本上，当你将parallel()与limit()结合使用时，你要求随机输出 - 这可能对你正在解决的问题很好。但是当你这样做时，你必须明白。这是一个仅限专家的功能，而不是要争辩说“Java弄错了”。

什么是更合理的方法来解决问题？好吧，如果你想生成一个int流，你可以使用IntStream.range()，如下所示：

```java
// concurrent/ParallelStreamPuzzle3.java
// {VisuallyInspectOutput}
import java.util.*;
import java.util.stream.*;
public class ParallelStreamPuzzle3 {
    public static void main(String[] args) {
    List<Integer> x = IntStream.range(0, 30)
        .peek(e -> System.out.println(e + ": " +Thread.currentThread()
        .getName()))
        .limit(10)
        .parallel()
        .boxed()
        .collect(Collectors.toList());
        System.out.println(x);
    }
}
```

输出结果：

```
8: main
6: ForkJoinPool.commonPool-worker-5
3: ForkJoinPool.commonPool-worker-7
5: ForkJoinPool.commonPool-worker-5
1: ForkJoinPool.commonPool-worker-3
2: ForkJoinPool.commonPool-worker-6
4: ForkJoinPool.commonPool-worker-1
0: ForkJoinPool.commonPool-worker-4
7: ForkJoinPool.commonPool-worker-1
9: ForkJoinPool.commonPool-worker-2
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
```

为了表明**parallel()**确实有效，我添加了一个对**peek()**的调用，这是一个主要用于调试的流函数：它从流中提取一个值并执行某些操作但不影响从流向下传递的元素。注意这会干扰线程行为，但我只是尝试在这里做一些事情，而不是实际调试任何东西。

你还可以看到boxed()的添加，它接受int流并将其转换为Integer流。

现在我们得到多个线程产生不同的值，但它只产生10个请求的值，而不是1024个产生10个值。

它更快吗？一个更好的问题是：什么时候开始有意义？当然不是这么小的一套;上下文切换的代价远远超过并行性的任何加速。当一个简单的数字序列并行生成时，有点难以想象。如果你使用昂贵的产品，它可能有意义 - 但这都是猜测。唯一知道的是通过测试。记住这句格言：“首先制作它，然后快速制作 - 但只有你必须这样做。”**parallel()**和**limit()**仅供专家使用（并且要清楚，我不认为自己是这里的专家）。

- 并行流只看起来很容易

实际上，在许多情况下，并行流确实可以毫不费力地更快地产生结果。但正如你所见，只需将**parallel()**打到你的Stream操作上并不一定是安全的事情。在使用**parallel()**之前，你必须了解并行性如何帮助或损害你的操作。有个错误认识是认为并行性总是一个好主意。事实上并不是。Stream意味着你不需要重写所有代码以便并行运行它。流什么都不做的是取代理解并行性如何工作的需要，以及它是否有助于实现你的目标。

## 创建和运行任务

如果无法通过并行流实现并发，则必须创建并运行自己的任务。稍后你将看到运行任务的理想Java 8方法是CompletableFuture，但我们将使用更基本的工具介绍概念。

Java并发的历史始于非常原始和有问题的机制，并且充满了各种尝试的改进。这些主要归入附录：[低级并发(Appendix: Low-Level Concurrency)](./Appendix-Low-Level-Concurrency.md)。在这里，我们将展示一个规范形式，表示创建和运行任务的最简单，最好的方法。与并发中的所有内容一样，存在各种变体，但这些变体要么降级到该附录，要么超出本书的范围。

- Tasks and Executors

在Java的早期版本中，你通过直接创建自己的Thread对象来使用线程，甚至将它们子类化以创建你自己的特定“任务线程”对象。你手动调用了构造函数并自己启动了线程。

创建所有这些线程的开销变得非常重要，现在不鼓励采用实际操作方法。在Java 5中，添加了类来为你处理线程池。你可以将任务创建为单独的类型，然后将其交给ExecutorService以运行该任务，而不是为每种不同类型的任务创建新的Thread子类型。ExecutorService为你管理线程，并且在运行任务后重新循环线程而不是丢弃线程。

首先，我们将创建一个几乎不执行任务的任务。它“sleep”（暂停执行）100毫秒，显示其标识符和正在执行任务的线程的名称，然后完成：

```java
// concurrent/NapTask.java
import onjava.Nap;
public class NapTask implements Runnable {
    finalint id;
    public NapTask(int id) {
        this.id = id;
        }
    @Override
    public void run() {
        new Nap(0.1);// Seconds
        System.out.println(this + " "+
            Thread.currentThread().getName());
        }
    @Override
    public String toString() {
        return"NapTask[" + id + "]";
    }
}
```

这只是一个**Runnable**：一个包含**run()**方法的类。它没有包含实际运行任务的机制。我们使用**Nap**类中的“sleep”：

```java
// onjava/Nap.java
package onjava;
import java.util.concurrent.*;
public class Nap {
    public Nap(double t) { // Seconds
        try {
            TimeUnit.MILLISECONDS.sleep((int)(1000 * t));
        } catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    public Nap(double t, String msg) {
        this(t);
        System.out.println(msg);
    }
}
```
为了消除异常处理的视觉噪声，这被定义为实用程序。第二个构造函数在超时时显示一条消息

对**TimeUnit.MILLISECONDS.sleep()**的调用获取“当前线程”并在参数中将其置于休眠状态，这意味着该线程被挂起。这并不意味着底层处理器停止。操作系统将其切换到其他任务，例如在你的计算机上运行另一个窗口。OS任务管理器定期检查**sleep()**是否超时。当它执行时，线程被“唤醒”并给予更多处理时间。

你可以看到**sleep()**抛出一个已检查的**InterruptedException**;这是原始Java设计中的一个工件，它通过突然断开它们来终止任务。因为它往往会产生不稳定的状态，所以后来不鼓励终止。但是，我们必须在需要或仍然发生终止的情况下捕获异常。

要执行任务，我们将从最简单的方法--SingleThreadExecutor开始:

```java
//concurrent/SingleThreadExecutor.java
import java.util.concurrent.*;
import java.util.stream.*;
import onjava.*;
public class SingleThreadExecutor {
    public static void main(String[] args) {
        ExecutorService exec =
            Executors.newSingleThreadExecutor();
        IntStream.range(0, 10)
            .mapToObj(NapTask::new)
            .forEach(exec::execute);
        System.out.println("All tasks submitted");
        exec.shutdown();
        while(!exec.isTerminated()) {
            System.out.println(
            Thread.currentThread().getName()+
            " awaiting termination");
            new Nap(0.1);
        }
    }
}
```

输出结果：

```
All tasks submitted
main awaiting termination
main awaiting termination
NapTask[0] pool-1-thread-1
main awaiting termination
NapTask[1] pool-1-thread-1
main awaiting termination
NapTask[2] pool-1-thread-1
main awaiting termination
NapTask[3] pool-1-thread-1
main awaiting termination
NapTask[4] pool-1-thread-1
main awaiting termination
NapTask[5] pool-1-thread-1
main awaiting termination
NapTask[6] pool-1-thread-1
main awaiting termination
NapTask[7] pool-1-thread-1
main awaiting termination
NapTask[8] pool-1-thread-1
main awaiting termination
NapTask[9] pool-1-thread-1
```

首先请注意，没有**SingleThreadExecutor**类。**newSingleThreadExecutor()**是**Executors**中的工厂，它创建特定类型的[^4]

我创建了十个NapTasks并将它们提交给ExecutorService，这意味着它们开始自己运行。然而，在此期间，main()继续做事。当我运行callexec.shutdown()时，它告诉ExecutorService完成已经提交的任务，但不接受任何新任务。此时，这些任务仍然在运行，因此我们必须等到它们在退出main()之前完成。这是通过检查exec.isTerminated()来实现的，这在所有任务完成后变为true。

请注意，main()中线程的名称是main，并且只有一个其他线程pool-1-thread-1。此外，交错输出显示两个线程确实同时运行。

如果你只是调用exec.shutdown()，程序将完成所有任务。也就是说，虽然不需要（！exec.isTerminated()）。

```java
// concurrent/SingleThreadExecutor2.java
import java.util.concurrent.*;
import java.util.stream.*;
public class SingleThreadExecutor2 {
    public static void main(String[] args)throws InterruptedException {
        ExecutorService exec
        =Executors.newSingleThreadExecutor();
        IntStream.range(0, 10)
            .mapToObj(NapTask::new)
            .forEach(exec::execute);
        exec.shutdown();
    }
}
```

输出结果：

```
NapTask[0] pool-1-thread-1
NapTask[1] pool-1-thread-1
NapTask[2] pool-1-thread-1
NapTask[3] pool-1-thread-1
NapTask[4] pool-1-thread-1
NapTask[5] pool-1-thread-1
NapTask[6] pool-1-thread-1
NapTask[7] pool-1-thread-1
NapTask[8] pool-1-thread-1
NapTask[9] pool-1-thread-1
```

一旦你callexec.shutdown()，尝试提交新任务将抛出RejectedExecutionException。

```java
// concurrent/MoreTasksAfterShutdown.java
import java.util.concurrent.*;
public class MoreTasksAfterShutdown {
    public static void main(String[] args) {
        ExecutorService exec
        =Executors.newSingleThreadExecutor();
        exec.execute(newNapTask(1));
        exec.shutdown();
        try {
            exec.execute(newNapTask(99));
        } catch(RejectedExecutionException e) {
            System.out.println(e);
        }
    }
}
```

输出结果：

```
java.util.concurrent.RejectedExecutionException: TaskNapTask[99] rejected from java.util.concurrent.ThreadPoolExecutor@4e25154f[Shutting down, pool size = 1,active threads = 1, queued tasks = 0, completed tasks =0]NapTask[1] pool-1-thread-1
```

**exec.shutdown()**的替代方法是**exec.shutdownNow()**，它除了不接受新任务外，还会尝试通过中断任务来停止任何当前正在运行的任务。同样，中断是错误的，容易出错并且不鼓励。

- 使用更多线程

使用线程的重点是（几乎总是）更快地完成任务，那么我们为什么要限制自己使用SingleThreadExecutor呢？查看执行**Executors**的Javadoc，你将看到更多选项。例如CachedThreadPool：

```java
// concurrent/CachedThreadPool.java
import java.util.concurrent.*;
import java.util.stream.*;
public class CachedThreadPool {
    public static void main(String[] args) {
        ExecutorService exec
        =Executors.newCachedThreadPool();
        IntStream.range(0, 10)
        .mapToObj(NapTask::new)
        .forEach(exec::execute);
        exec.shutdown();
    }
}
```

输出结果：

```
NapTask[7] pool-1-thread-8
NapTask[4] pool-1-thread-5
NapTask[1] pool-1-thread-2
NapTask[3] pool-1-thread-4
NapTask[0] pool-1-thread-1
NapTask[8] pool-1-thread-9
NapTask[2] pool-1-thread-3
NapTask[9] pool-1-thread-10
NapTask[6] pool-1-thread-7
NapTask[5] pool-1-thread-6
```

当你运行这个程序时，你会发现它完成得更快。这是有道理的，而不是使用相同的线程来顺序运行每个任务，每个任务都有自己的线程，所以它们都并行运行。似乎没有缺点，很难看出为什么有人会使用SingleThreadExecutor。

要理解这个问题，我们需要一个更复杂的任务：

```java
// concurrent/InterferingTask.java
public class InterferingTask implements Runnable {
    final int id;
    private static Integer val = 0;
    public InterferingTask(int id) {
        this.id = id;
    }
    @Override
    public void run() {
        for(int i = 0; i < 100; i++)
        val++;
    System.out.println(id + " "+
        Thread.currentThread().getName() + " " + val);
    }
}

```

每个任务增加val一百次。这似乎很简单。让我们用CachedThreadPool尝试一下：

```java
// concurrent/CachedThreadPool2.java
import java.util.concurrent.*;
import java.util.stream.*;
public class CachedThreadPool2 {
    public static void main(String[] args) {
    ExecutorService exec
    =Executors.newCachedThreadPool();
    IntStream.range(0, 10)
    .mapToObj(InterferingTask::new)
    .forEach(exec::execute);
    exec.shutdown();
    }
}
```

输出结果：

```
0 pool-1-thread-1 200
1 pool-1-thread-2 200
4 pool-1-thread-5 300
5 pool-1-thread-6 400
8 pool-1-thread-9 500
9 pool-1-thread-10 600
2 pool-1-thread-3 700
7 pool-1-thread-8 800
3 pool-1-thread-4 900
6 pool-1-thread-7 1000
```

输出不是我们所期望的，并且从一次运行到下一次运行会有所不同。问题是所有的任务都试图写入val的单个实例，并且他们正在踩着彼此的脚趾。我们说这样的类不是线程安全的。让我们看看SingleThreadExecutor会发生什么：

```java
// concurrent/SingleThreadExecutor3.java
import java.util.concurrent.*;
import java.util.stream.*;
public class SingleThreadExecutor3 {
    public static void main(String[] args)throws InterruptedException {
        ExecutorService exec
        =Executors.newSingleThreadExecutor();
        IntStream.range(0, 10)
        .mapToObj(InterferingTask::new)
        .forEach(exec::execute);
        exec.shutdown();
    }
}
```

输出结果：

```
0 pool-1-thread-1 100
1 pool-1-thread-1 200
2 pool-1-thread-1 300
3 pool-1-thread-1 400
4 pool-1-thread-1 500
5 pool-1-thread-1 600
6 pool-1-thread-1 700
7 pool-1-thread-1 800
8 pool-1-thread-1 900
9 pool-1-thread-1 1000
```

现在我们每次都得到一致的结果，尽管**InterferingTask**缺乏线程安全性。这是SingleThreadExecutor的主要好处 - 因为它一次运行一个任务，这些任务不会相互干扰，因此强加了线程安全性。这种现象称为线程限制，因为在单线程上运行任务限制了它们的影响。线程限制限制了加速，但可以节省很多困难的调试和重写。

- 产生结果

因为**InterferingTask**是一个**Runnable**，它没有返回值，因此只能使用副作用产生结果 - 操纵缓冲值而不是返回结果。副作用是并发编程中的主要问题之一，因为我们看到了**CachedThreadPool2.java**。**InterferingTask**中的**val**被称为可变共享状态，这就是问题所在：多个任务同时修改同一个变量会产生竞争。结果取决于首先在终点线上执行哪个任务，并修改变量（以及其他可能性的各种变化）。

避免竞争条件的最好方法是避免可变的共享状态。我们可以称之为自私的孩子原则：什么都不分享。

使用**InterferingTask**，最好删除副作用并返回任务结果。为此，我们创建**Callable**而不是**Runnable**：

```java
// concurrent/CountingTask.java
import java.util.concurrent.*;
public class CountingTask implements Callable<Integer> {
    final int id;
    public CountingTask(int id) { this.id = id; }
    @Override
    public Integer call() {
    Integer val = 0;
    for(int i = 0; i < 100; i++)
        val++;
    System.out.println(id + " " +
        Thread.currentThread().getName() + " " + val);
    return val;
    }
}

```

**call()完全独立于所有其他CountingTasks生成其结果**，这意味着没有可变的共享状态

**ExecutorService**允许你使用**invokeAll()**启动集合中的每个Callable：

```java
// concurrent/CachedThreadPool3.java
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
public class CachedThreadPool3 {
    public static Integer extractResult(Future<Integer> f) {
        try {
            return f.get();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args)throws InterruptedException {
    ExecutorService exec =
    Executors.newCachedThreadPool();
    List<CountingTask> tasks =
        IntStream.range(0, 10)
            .mapToObj(CountingTask::new)
            .collect(Collectors.toList());
        List<Future<Integer>> futures =
            exec.invokeAll(tasks);
        Integer sum = futures.stream()
            .map(CachedThreadPool3::extractResult)
            .reduce(0, Integer::sum);
        System.out.println("sum = " + sum);
        exec.shutdown();
    }
}
```

输出结果：

```
1 pool-1-thread-2 100
0 pool-1-thread-1 100
4 pool-1-thread-5 100
5 pool-1-thread-6 100
8 pool-1-thread-9 100
9 pool-1-thread-10 100
2 pool-1-thread-3 100
3 pool-1-thread-4 100
6 pool-1-thread-7 100
7 pool-1-thread-8 100
sum = 1000

```

只有在所有任务完成后，**invokeAll()**才会返回一个**Future**列表，每个任务一个**Future**。**Future**是Java 5中引入的机制，允许你提交任务而无需等待它完成。在这里，我们使用**ExecutorService.submit()**:

```java
// concurrent/Futures.java
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
public class Futures {
    public static void main(String[] args)throws InterruptedException, ExecutionException {
    ExecutorService exec
        =Executors.newSingleThreadExecutor();
    Future<Integer> f =
        exec.submit(newCountingTask(99));
    System.out.println(f.get()); // [1]
    exec.shutdown();
    }
}
```

输出结果：

```
99 pool-1-thread-1 100
100
```

- [1] 当你的任务尚未完成的**Future**上调用**get()**时，调用会阻塞（等待）直到结果可用。

但这意味着，在**CachedThreadPool3.java**中，**Future**似乎是多余的，因为**invokeAll()**甚至在所有任务完成之前都不会返回。但是，这里的Future并不用于延迟结果，而是用于捕获任何可能发生的异常。

还要注意在**CachedThreadPool3.java.get()**中抛出异常，因此**extractResult()**在Stream中执行此提取。

因为当你调用**get()**时，**Future**会阻塞，所以它只能解决等待任务完成的问题。最终，**Futures**被认为是一种无效的解决方案，现在不鼓励，支持Java 8的**CompletableFuture**，我们将在本章后面探讨。当然，你仍会在遗留库中遇到Futures

我们可以使用并行Stream以更简单，更优雅的方式解决这个问题:

```java
// concurrent/CountingStream.java
// {VisuallyInspectOutput}
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
public class CountingStream {
    public static void main(String[] args) {
        System.out.println(
            IntStream.range(0, 10)
                .parallel()
                .mapToObj(CountingTask::new)
                .map(ct -> ct.call())
                .reduce(0, Integer::sum));
    }
}
```

输出结果：

```
1 ForkJoinPool.commonPool-worker-3 100
8 ForkJoinPool.commonPool-worker-2 100
0 ForkJoinPool.commonPool-worker-6 100
2 ForkJoinPool.commonPool-worker-1 100
4 ForkJoinPool.commonPool-worker-5 100
9 ForkJoinPool.commonPool-worker-7 100
6 main 100
7 ForkJoinPool.commonPool-worker-4 100
5 ForkJoinPool.commonPool-worker-2 100
3 ForkJoinPool.commonPool-worker-3 100
1000
```

这不仅更容易理解，我们需要做的就是将**parallel()**插入到其他顺序操作中，然后一切都在同时运行。

- Lambda和方法引用作为任务

在 `java8` , 你不需要受限于在  `Runnables ` 和 `Callables` 时，使用`lambdas` 和方法引用， 同样也可以通过匹配签名来引用（即，它支持结构一致性）。 所以我们可以将 `notRunnables` 或 `Callables` 的参数传递给`ExecutorService` : 

```java
// concurrent/LambdasAndMethodReferences.java
import java.util.concurrent.*;
class NotRunnable {
    public void go() {
        System.out.println("NotRunnable");
    }
}
class NotCallable {
    public Integer get() {
        System.out.println("NotCallable");
        return 1;
    }
}
public class LambdasAndMethodReferences {
    public static void main(String[] args)throws InterruptedException {
    ExecutorService exec =
        Executors.newCachedThreadPool();
    exec.submit(() -> System.out.println("Lambda1"));
    exec.submit(new NotRunnable()::go);
    exec.submit(() -> {
        System.out.println("Lambda2");
        return 1;
    });
    exec.submit(new NotCallable()::get);
    exec.shutdown();
    }
}
```

输出结果：

```
Lambda1
NotCallable
NotRunnable
Lambda2

```

这里，前两个**submit()**调用可以改为调用**execute()**。所有**submit()**调用都返回**Futures**，你可以在后两次调用的情况下提取结果。

<!-- Terminating Long-Running Tasks -->
## 终止耗时任务

并发程序通常使用长时间运行的任务。可调用任务在完成时返回值;虽然这给它一个有限的寿命，但仍然可能很长。可运行的任务有时被设置为永远运行的后台进程。你经常需要一种方法在正常完成之前停止**Runnable**和**Callable**任务，例如当你关闭程序时。

最初的Java设计提供了中断运行任务的机制（为了向后兼容，仍然存在）;中断机制包括阻塞问题。中断任务既乱又复杂，因为你必须了解可能发生中断的所有可能状态，以及可能导致的数据丢失。使用中断被视为反对模式，但我们仍然被迫接受。

InterruptedException，因为设计的向后兼容性残留。

任务终止的最佳方法是设置任务周期性检查的标志。然后任务可以通过自己的shutdown进程并正常终止。不是在任务中随机关闭线程，而是要求任务在到达了一个较好时自行终止。这总是产生比中断更好的结果，以及更容易理解的更合理的代码。

以这种方式终止任务听起来很简单：设置任务可以看到的**boolean** flag。编写任务，以便定期检查标志并执行正常终止。这实际上就是你所做的，但是有一个复杂的问题：我们的旧克星，共同的可变状态。如果该标志可以被另一个任务操纵，则存在碰撞可能性。

在研究Java文献时，你会发现很多解决这个问题的方法，经常使用**volatile**关键字。我们将使用更简单的技术并避免所有易变的参数，这些都在[附录：低级并发](./Appendix-Low-Level-Concurrency.md)中有所涉及。

Java 5引入了**Atomic**类，它提供了一组可以使用的类型，而不必担心并发问题。我们将添加**AtomicBoolean**标志，告诉任务清理自己并退出。

```java
// concurrent/QuittableTask.java
import java.util.concurrent.atomic.AtomicBoolean;import onjava.Nap;
public class QuittableTask implements Runnable {
    final int id;
    public QuittableTask(int id) {
        this.id = id;
    }
    private AtomicBoolean running =
        new AtomicBoolean(true);
    public void quit() {
        running.set(false);
    }
    @Override
    public void run() {
        while(running.get())         // [1]
            new Nap(0.1);
        System.out.print(id + " ");  // [2]
    }
}

```

虽然多个任务可以在同一个实例上成功调用**quit()**，但是**AtomicBoolean**可以防止多个任务同时实际修改**running**，从而使**quit()**方法成为线程安全的。

- [1]:只要运行标志为true，此任务的run()方法将继续。
- [2]: 显示仅在任务退出时发生。

需要**running AtomicBoolean**证明编写Java program并发时最基本的困难之一是，如果**running**是一个普通的布尔值，你可能无法在执行程序中看到问题。实际上，在这个例子中，你可能永远不会有任何问题 - 但是代码仍然是不安全的。编写表明该问题的测试可能很困难或不可能。因此，你没有任何反馈来告诉你已经做错了。通常，你编写线程安全代码的唯一方法就是通过了解事情可能出错的所有细微之处。

作为测试，我们将启动很多QuittableTasks然后关闭它们。尝试使用较大的COUNT值

```java
// concurrent/QuittingTasks.java
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import onjava.Nap;
public class QuittingTasks {
    public static final int COUNT = 150;
    public static void main(String[] args) {
    ExecutorService es =
    Executors.newCachedThreadPool();
    List<QuittableTask> tasks =
    IntStream.range(1, COUNT)
        .mapToObj(QuittableTask::new)
        .peek(qt -> es.execute(qt))
        .collect(Collectors.toList());
    new Nap(1);
    tasks.forEach(QuittableTask::quit);    es.shutdown();
    }
}
```

输出结果：

```
24 27 31 8 11 7 19 12 16 4 23 3 28 32 15 20 63 60 68 6764 39 47 52 51 55 40 43 48 59 44 56 36 35 71 72 83 10396 92 88 99 100 87 91 79 75 84 76 115 108 112 104 107111 95 80 147 120 127 119 123 144 143 116 132 124 128
136 131 135 139 148 140 2 126 6 5 1 18 129 17 14 13 2122 9 10 30 33 58 37 125 26 34 133 145 78 137 141 138 6274 142 86 65 73 146 70 42 149 121 110 134 105 82 117106 113 122 45 114 118 38 50 29 90 101 89 57 53 94 4161 66 130 69 77 81 85 93 25 102 54 109 98 49 46 97
```

我使用**peek()**将**QuittableTasks**传递给**ExecutorService**，然后将这些任务收集到**List.main()**中，只要任何任务仍在运行，就会阻止程序退出。即使为每个任务按顺序调用quit()方法，任务也不会按照它们创建的顺序关闭。独立运行的任务不会确定性地响应信号。

## CompletableFuture类

作为介绍，这里是使用CompletableFutures在QuittingTasks.java中：

```java
// concurrent/QuittingCompletable.java
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import onjava.Nap;
public class QuittingCompletable {
    public static void main(String[] args) {
    List<QuittableTask> tasks =
        IntStream.range(1, QuittingTasks.COUNT)
            .mapToObj(QuittableTask::new)
            .collect(Collectors.toList());
        List<CompletableFuture<Void>> cfutures =
        tasks.stream()
            .map(CompletableFuture::runAsync)
            .collect(Collectors.toList());
        new Nap(1);
        tasks.forEach(QuittableTask::quit);
        cfutures.forEach(CompletableFuture::join);
    }
}
```

输出结果：

```
7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 2526 27 28 29 30 31 32 33 34 6 35 4 38 39 40 41 42 43 4445 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 6263 64 65 66 67 68 69 70 71 72 73 74 75 76 77 78 79 8081 82 83 84 85 86 87 88 89 90 91 92 93 94 95 96 97 9899 100 101 102 103 104 105 106 107 108 109 110 111 1121 113 114 116 117 118 119 120 121 122 123 124 125 126127 128 129 130 131 132 133 134 135 136 137 138 139 140141 142 143 144 145 146 147 148 149 5 115 37 36 2 3
```

任务是一个 `List<QuittableTask>`，就像在 `QuittingTasks.java` 中一样，但是在这个例子中，没有 `peek()` 将每个 `QuittableTask` 提交给 `ExecutorService`。相反，在创建 `cfutures` 期间，每个任务都交给 `CompletableFuture::runAsync`。这执行 `VerifyTask.run()` 并返回 `CompletableFuture<Void>` 。因为 `run()` 不返回任何内容，所以在这种情况下我只使用 `CompletableFuture` 调用 `join()` 来等待它完成。

在本例中需要注意的重要一点是，运行任务不需要使用 `ExecutorService`。而是直接交给 `CompletableFuture` 管理 (不过你可以向它提供自己定义的 `ExectorService`)。您也不需要调用 `shutdown()`;事实上，除非你像我在这里所做的那样显式地调用 `join()`，否则程序将尽快退出，而不必等待任务完成。

这个例子只是一个起点。你很快就会看到 `ComplempleFuture` 能够做得更多。

### 基本用法

这是一个带有静态方法**work()**的类，它对该类的对象执行某些工作：

```java
// concurrent/Machina.java
import onjava.Nap;
public class Machina {
    public enum State {
        START, ONE, TWO, THREE, END;
        State step() {
            if(equals(END))
            return END;
          return values()[ordinal() + 1];
        }
    }
    private State state = State.START;
    private final int id;
    public Machina(int id) {
        this.id = id;
    }
    public static Machina work(Machina m) {
        if(!m.state.equals(State.END)){
            new Nap(0.1);
            m.state = m.state.step();
        }
        System.out.println(m);
        return m;
    }
    @Override
    public String toString() {
        return"Machina" + id + ": " + (state.equals(State.END)? "complete" : state);
    }
}

```

这是一个有限状态机，一个微不足道的机器，因为它没有分支......它只是从头到尾遍历一条路径。**work()**方法将机器从一个状态移动到下一个状态，并且需要100毫秒才能完成“工作”。

**CompletableFuture**可以被用来做的一件事是, 使用**completedFuture()**将它感兴趣的对象进行包装。

```java
// concurrent/CompletedMachina.java
import java.util.concurrent.*;
public class CompletedMachina {
    public static void main(String[] args) {
        CompletableFuture<Machina> cf =
        CompletableFuture.completedFuture(
            new Machina(0));
        try {
            Machina m = cf.get();  // Doesn't block
        } catch(InterruptedException |
            ExecutionException e) {
        throw new RuntimeException(e);
        }
    }
}
```

**completedFuture()**创建一个“已经完成”的**CompletableFuture**。对这样一个未来做的唯一有用的事情是**get()**里面的对象，所以这看起来似乎没有用。注意**CompletableFuture**被输入到它包含的对象。这个很重要。

通常，**get()**在等待结果时阻塞调用线程。此块可以通过**InterruptedException**或**ExecutionException**中断。在这种情况下，阻止永远不会发生，因为CompletableFutureis已经完成，所以答案立即可用。

当我们将**handle()**包装在**CompletableFuture**中时，发现我们可以在**CompletableFuture**上添加操作来处理所包含的对象，使得事情变得更加有趣：

```java
// concurrent/CompletableApply.java
import java.util.concurrent.*;
public class CompletableApply {
    public static void main(String[] args) {
        CompletableFuture<Machina> cf =
        CompletableFuture.completedFuture(
            new Machina(0));
        CompletableFuture<Machina> cf2 =
            cf.thenApply(Machina::work);
        CompletableFuture<Machina> cf3 =
            cf2.thenApply(Machina::work);
        CompletableFuture<Machina> cf4 =
            cf3.thenApply(Machina::work);
        CompletableFuture<Machina> cf5 =
            cf4.thenApply(Machina::work);
    }
}
```

**输出结果**：

```
Machina0: ONE
Machina0: TWO
Machina0: THREE
Machina0: complete
```

`thenApply()` 应用一个接收输入并产生输出的函数。在本例中，`work()` 函数产生的类型与它所接收的类型相同 （`Machina`），因此每个 `CompletableFuture`添加的操作的返回类型都为 `Machina`，但是(类似于流中的 `map()` )函数也可以返回不同的类型，这将体现在返回类型上。

你可以在此处看到有关**CompletableFutures**的重要信息：它们会在你执行操作时自动解包并重新包装它们所携带的对象。这使得编写和理解代码变得更加简单， 而不会在陷入在麻烦的细节中。

我们可以消除中间变量并将操作链接在一起，就像我们使用Streams一样：

```java
// concurrent/CompletableApplyChained.javaimport java.util.concurrent.*;
import onjava.Timer;
public class CompletableApplyChained {
    public static void main(String[] args) {
        Timer timer = new Timer();
        CompletableFuture<Machina> cf =
        CompletableFuture.completedFuture(
            new Machina(0))
                  .thenApply(Machina::work)
                  .thenApply(Machina::work)
                  .thenApply(Machina::work)
                  .thenApply(Machina::work);
        System.out.println(timer.duration());
    }
}
```

输出结果：

```
Machina0: ONE
Machina0: TWO
Machina0: THREE
Machina0: complete
514
```

这里我们还添加了一个 `Timer`，它的功能在每一步都显性地增加 100ms 等待时间之外，还将 `CompletableFuture` 内部 `thenApply` 带来的额外开销给体现出来了。 
**CompletableFutures** 的一个重要好处是它们鼓励使用私有子类原则（不共享任何东西）。默认情况下，使用 **thenApply()** 来应用一个不对外通信的函数 - 它只需要一个参数并返回一个结果。这是函数式编程的基础，并且它在并发特性方面非常有效[^5]。并行流和 `ComplempleFutures` 旨在支持这些原则。只要你不决定共享数据（共享非常容易导致意外发生）你就可以编写出相对安全的并发程序。

回调 `thenApply()` 一旦开始一个操作，在完成所有任务之前，不会完成 **CompletableFuture** 的构建。虽然这有时很有用，但是开始所有任务通常更有价值，这样就可以运行继续前进并执行其他操作。我们可通过`thenApplyAsync()` 来实现此目的：

```java
// concurrent/CompletableApplyAsync.java
import java.util.concurrent.*;
import onjava.*;
public class CompletableApplyAsync {
    public static void main(String[] args) {
        Timer timer = new Timer();
        CompletableFuture<Machina> cf =
            CompletableFuture.completedFuture(
                new Machina(0))
                .thenApplyAsync(Machina::work)
                .thenApplyAsync(Machina::work)
                .thenApplyAsync(Machina::work)
                .thenApplyAsync(Machina::work);
            System.out.println(timer.duration());
            System.out.println(cf.join());
            System.out.println(timer.duration());
    }
}
```

输出结果：

```
116
Machina0: ONE
Machina0: TWO
Machina0:THREE
Machina0: complete
Machina0: complete
552
```

同步调用(我们通常使用的那种)意味着：“当你完成工作时，才返回”，而异步调用以意味着： “立刻返回并继续后续工作”。 正如你所看到的，`cf` 的创建现在发生的更快。每次调用 `thenApplyAsync()` 都会立刻返回，因此可以进行下一次调用，整个调用链路完成速度比以前快得多。

事实上，如果没有回调 `cf.join()` 方法，程序会在完成其工作之前退出。而 `cf.join()` 直到cf操作完成之前，阻止 `main()` 进程结束。我们还可以看出本示例大部分时间消耗在 `cf.join()` 这。

这种“立即返回”的异步能力需要 `CompletableFuture` 库进行一些秘密（`client` 无感）工作。特别是，它将你需要的操作链存储为一组回调。当操作的第一个链路（后台操作）完成并返回时，第二个链路（后台操作）必须获取生成的 `Machina` 并开始工作，以此类推！ 但这种异步机制没有我们可以通过程序调用栈控制的普通函数调用序列，它的调用链路顺序会丢失，因此它使用一个函数地址来存储的回调来解决这个问题。

幸运的是，这就是你需要了解的有关回调的全部信息。程序员将这种人为制造的混乱称为 callback hell(回调地狱)。通过异步调用，`CompletableFuture` 帮你管理所有回调。 除非你知道系统的一些具体的变化，否则你更想使用异步调用来实现程序。

- 其他操作

当你查看`CompletableFuture`的 `Javadoc` 时，你会看到它有很多方法，但这个方法的大部分来自不同操作的变体。例如，有 `thenApply()`，`thenApplyAsync()` 和第二种形式的 `thenApplyAsync()`，它们使用 `Executor` 来运行任务(在本书中，我们忽略了 `Executor` 选项)。

下面的示例展示了所有"基本"操作，这些操作既不涉及组合两个 `CompletableFuture`，也不涉及异常(我们将在后面介绍)。首先，为了提供简洁性和方便性，我们应该重用以下两个实用程序:

```java
package onjava;
import java.util.concurrent.*;

public class CompletableUtilities {
  // Get and show value stored in a CF:
  public static void showr(CompletableFuture<?> c) {
    try {
      System.out.println(c.get());
    } catch(InterruptedException
            | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
  // For CF operations that have no value:
  public static void voidr(CompletableFuture<Void> c) {
    try {
      c.get(); // Returns void
    } catch(InterruptedException
            | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
```

`showr()` 在 `CompletableFuture<Integer>` 上调用 `get()`，并显示结果，`try/catch` 两个可能会出现的异常。

`voidr()` 是 `CompletableFuture<Void>` 的 `showr()` 版本，也就是说，`CompletableFutures` 只为任务完成或失败时显示信息。

为简单起见，下面的 `CompletableFutures` 只包装整数。`cfi()` 是一个便利的方法，它把一个整数包装在一个完整的 `CompletableFuture<Integer>` :

```java
// concurrent/CompletableOperations.java
import java.util.concurrent.*;
import static onjava.CompletableUtilities.*;

public class CompletableOperations {
    static CompletableFuture<Integer> cfi(int i) {
        return
                CompletableFuture.completedFuture(
                        Integer.valueOf(i));
    }

    public static void main(String[] args) {
        showr(cfi(1)); // Basic test
        voidr(cfi(2).runAsync(() ->
                System.out.println("runAsync")));
        voidr(cfi(3).thenRunAsync(() ->
                System.out.println("thenRunAsync")));
        voidr(CompletableFuture.runAsync(() ->
                System.out.println("runAsync is static")));
        showr(CompletableFuture.supplyAsync(() -> 99));
        voidr(cfi(4).thenAcceptAsync(i ->
                System.out.println("thenAcceptAsync: " + i)));
        showr(cfi(5).thenApplyAsync(i -> i + 42));
        showr(cfi(6).thenComposeAsync(i -> cfi(i + 99)));
        CompletableFuture<Integer> c = cfi(7);
        c.obtrudeValue(111);
        showr(c);
        showr(cfi(8).toCompletableFuture());
        c = new CompletableFuture<>();
        c.complete(9);
        showr(c);
        c = new CompletableFuture<>();
        c.cancel(true);
        System.out.println("cancelled: " +
                c.isCancelled());
        System.out.println("completed exceptionally: " +
                c.isCompletedExceptionally());
        System.out.println("done: " + c.isDone());
        System.out.println(c);
        c = new CompletableFuture<>();
        System.out.println(c.getNow(777));
        c = new CompletableFuture<>();
        c.thenApplyAsync(i -> i + 42)
                .thenApplyAsync(i -> i * 12);
        System.out.println("dependents: " +
                c.getNumberOfDependents());
        c.thenApplyAsync(i -> i / 2);
        System.out.println("dependents: " +
                c.getNumberOfDependents());
    }
}
```

**输出结果** ：

```
1
runAsync
thenRunAsync
runAsync is static
99
thenAcceptAsync: 4
47
105
111
8
9
cancelled: true
completed exceptionally: true
done: true
java.util.concurrent.CompletableFuture@6d311334[Complet ed exceptionally]
777
dependents: 1
dependents: 2
```

- `main()` 包含一系列可由其 `int` 值引用的测试。
  - `cfi(1)` 演示了 `showr()` 正常工作。
  - `cfi(2)` 是调用 `runAsync()` 的示例。由于 `Runnable` 不产生返回值，因此使用了返回 `CompletableFuture <Void>` 的`voidr()` 方法。
  - 注意使用 `cfi(3)`,`thenRunAsync()` 效果似乎与 上例 `cfi(2)` 使用的 `runAsync()`相同，差异在后续的测试中体现：
    - `runAsync()` 是一个 `static` 方法，所以你通常不会像`cfi(2)`一样调用它。相反你可以在 `QuittingCompletable.java` 中使用它。
    - 后续测试中表明 `supplyAsync()` 也是静态方法，区别在于它需要一个 `Supplier` 而不是`Runnable`, 并产生一个`CompletableFuture<Integer>` 而不是 `CompletableFuture<Void>`。
  - `then` 系列方法将对现有的 `CompletableFuture<Integer>` 进一步操作。
    - 与 `thenRunAsync()` 不同，`cfi(4)`，`cfi(5)` 和`cfi(6)` "then" 方法的参数是未包装的 `Integer`。
    - 通过使用 `voidr()`方法可以看到: 
      - `AcceptAsync()`接收了一个 `Consumer`，因此不会产生结果。
      - `thenApplyAsync()` 接收一个`Function`, 并生成一个结果（该结果的类型可以不同于其输入类型）。
      - `thenComposeAsync()` 与 `thenApplyAsync()`非常相似，唯一区别在于其 `Function` 必须产生已经包装在`CompletableFuture`中的结果。
  - `cfi(7)` 示例演示了 `obtrudeValue()`，它强制将值作为结果。
  - `cfi(8)` 使用 `toCompletableFuture()` 从 `CompletionStage` 生成一个`CompletableFuture`。
  - `c.complete(9)` 显示了如何通过给它一个结果来完成一个`task`（`future`）（与 `obtrudeValue()` 相对，后者可能会迫使其结果替换该结果）。
  - 如果你调用 `CompletableFuture`中的 `cancel()`方法，如果已经完成此任务，则正常结束。 如果尚未完成，则使用 `CancellationException` 完成此 `CompletableFuture`。
  - 如果任务（`future`）完成，则**getNow()**方法返回`CompletableFuture`的完成值，否则返回`getNow()`的替换参数。
  - 最后，我们看一下依赖(`dependents`)的概念。如果我们将两个`thenApplyAsync()`调用链路到`CompletableFuture`上，则依赖项的数量不会增加，保持为1。但是，如果我们另外将另一个`thenApplyAsync()`直接附加到`c`，则现在有两个依赖项：两个一起的链路和另一个单独附加的链路。
    - 这表明你可以使用一个`CompletionStage`，当它完成时，可以根据其结果派生多个新任务。



### 结合 CompletableFuture

第二种类型的 `CompletableFuture` 方法采用两种 `CompletableFuture` 并以各异方式将它们组合在一起。就像两个人在比赛一样, 一个`CompletableFuture`通常比另一个更早地到达终点。这些方法允许你以不同的方式处理结果。
为了测试这一点，我们将创建一个任务，它有一个我们可以控制的定义了完成任务所需要的时间量的参数。 
CompletableFuture 先完成:
```java
// concurrent/Workable.java
import java.util.concurrent.*;
import onjava.Nap;

public class Workable {
    String id;
    final double duration;

    public Workable(String id, double duration) {
        this.id = id;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Workable[" + id + "]";
    }

    public static Workable work(Workable tt) {
        new Nap(tt.duration); // Seconds
        tt.id = tt.id + "W";
        System.out.println(tt);
        return tt;
    }

    public static CompletableFuture<Workable> make(String id, double duration) {
        return CompletableFuture
                .completedFuture(
                        new Workable(id, duration)
                )
                .thenApplyAsync(Workable::work);
    }
}
```

在 `make()`中，`work()`方法应用于`CompletableFuture`。`work()`需要一定的时间才能完成，然后它将字母W附加到id上，表示工作已经完成。

现在我们可以创建多个竞争的 `CompletableFuture`，并使用 `CompletableFuture` 库中的各种方法来进行操作:

```java
// concurrent/DualCompletableOperations.java
import java.util.concurrent.*;
import static onjava.CompletableUtilities.*;

public class DualCompletableOperations {
    static CompletableFuture<Workable> cfA, cfB;

    static void init() {
        cfA = Workable.make("A", 0.15);
        cfB = Workable.make("B", 0.10); // Always wins
    }

    static void join() {
        cfA.join();
        cfB.join();
        System.out.println("*****************");
    }

    public static void main(String[] args) {
        init();
        voidr(cfA.runAfterEitherAsync(cfB, () ->
                System.out.println("runAfterEither")));
        join();

        init();
        voidr(cfA.runAfterBothAsync(cfB, () ->
                System.out.println("runAfterBoth")));
        join();

        init();
        showr(cfA.applyToEitherAsync(cfB, w -> {
            System.out.println("applyToEither: " + w);
            return w;
        }));
        join();

        init();
        voidr(cfA.acceptEitherAsync(cfB, w -> {
            System.out.println("acceptEither: " + w);
        }));
        join();

        init();
        voidr(cfA.thenAcceptBothAsync(cfB, (w1, w2) -> {
            System.out.println("thenAcceptBoth: "
                    + w1 + ", " + w2);
        }));
        join();

        init();
        showr(cfA.thenCombineAsync(cfB, (w1, w2) -> {
            System.out.println("thenCombine: "
                    + w1 + ", " + w2);
            return w1;
        }));
        join();

        init();
        CompletableFuture<Workable>
                cfC = Workable.make("C", 0.08),
                cfD = Workable.make("D", 0.09);
        CompletableFuture.anyOf(cfA, cfB, cfC, cfD)
                .thenRunAsync(() ->
                        System.out.println("anyOf"));
        join();

        init();
        cfC = Workable.make("C", 0.08);
        cfD = Workable.make("D", 0.09);
        CompletableFuture.allOf(cfA, cfB, cfC, cfD)
                .thenRunAsync(() ->
                        System.out.println("allOf"));
        join();
    }
}
```

**输出结果**：

```
Workable[BW]
runAfterEither
Workable[AW]
*****************
Workable[BW]
Workable[AW]
runAfterBoth
*****************
Workable[BW]
applyToEither: Workable[BW]
Workable[BW]
Workable[AW]
*****************
Workable[BW]
acceptEither: Workable[BW]
Workable[AW]
*****************
Workable[BW]
Workable[AW]
thenAcceptBoth: Workable[AW], Workable[BW]
****************
 Workable[BW]
 Workable[AW]
 thenCombine: Workable[AW], Workable[BW]
 Workable[AW]
 *****************
 Workable[CW]
 anyOf
 Workable[DW]
 Workable[BW]
 Workable[AW]
 *****************
 Workable[CW]
 Workable[DW]
 Workable[BW]
 Workable[AW]
 *****************
 allOf
```

- 为了方便访问， 将 `cfA` 和 `cfB` 定义为 `static`的。 
  - `init()`方法用于 `A`, `B` 初始化这两个变量，因为 `B` 总是给出比`A`较短的延迟，所以总是 `win` 的一方。
  - `join()` 是在两个方法上调用 `join()` 并显示边框的另一个便利方法。
- 所有这些 “`dual`” 方法都以一个 `CompletableFuture` 作为调用该方法的对象，第二个 `CompletableFuture` 作为第一个参数，然后是要执行的操作。
- 通过使用 `showr()` 和 `voidr()` 可以看到，“`run`”和“`accept`”是终端操作，而“`apply`”和“`combine`”则生成新的 `payload-bearing` (承载负载)的 `CompletableFuture`。
- 方法的名称不言自明，你可以通过查看输出来验证这一点。一个特别有趣的方法是 `combineAsync()`，它等待两个 `CompletableFuture` 完成，然后将它们都交给一个 `BiFunction`，这个 `BiFunction` 可以将结果加入到最终的 `CompletableFuture` 的有效负载中。


### 模拟

作为使用 `CompletableFuture` 将一系列操作组合的示例，让我们模拟一下制作蛋糕的过程。在第一阶段，我们准备并将原料混合成面糊:

```java
// concurrent/Batter.java
import java.util.concurrent.*;
import onjava.Nap;

public class Batter {
    static class Eggs {
    }

    static class Milk {
    }

    static class Sugar {
    }

    static class Flour {
    }

    static <T> T prepare(T ingredient) {
        new Nap(0.1);
        return ingredient;
    }

    static <T> CompletableFuture<T> prep(T ingredient) {
        return CompletableFuture
                .completedFuture(ingredient)
                .thenApplyAsync(Batter::prepare);
    }

    public static CompletableFuture<Batter> mix() {
        CompletableFuture<Eggs> eggs = prep(new Eggs());
        CompletableFuture<Milk> milk = prep(new Milk());
        CompletableFuture<Sugar> sugar = prep(new Sugar());
        CompletableFuture<Flour> flour = prep(new Flour());
        CompletableFuture
                .allOf(eggs, milk, sugar, flour)
                .join();
        new Nap(0.1); // Mixing time
        return CompletableFuture.completedFuture(new Batter());
    }
}
```

每种原料都需要一些时间来准备。`allOf()` 等待所有的配料都准备好，然后使用更多些的时间将其混合成面糊。接下来，我们把单批面糊放入四个平底锅中烘烤。产品作为 `CompletableFutures`  流返回：

```java
// concurrent/Baked.java

import java.util.concurrent.*;
import java.util.stream.*;
import onjava.Nap;

public class Baked {
    static class Pan {
    }

    static Pan pan(Batter b) {
        new Nap(0.1);
        return new Pan();
    }

    static Baked heat(Pan p) {
        new Nap(0.1);
        return new Baked();
    }

    static CompletableFuture<Baked> bake(CompletableFuture<Batter> cfb) {
        return cfb
                .thenApplyAsync(Baked::pan)
                .thenApplyAsync(Baked::heat);
    }

    public static Stream<CompletableFuture<Baked>> batch() {
        CompletableFuture<Batter> batter = Batter.mix();
        return Stream.of(
                bake(batter),
                bake(batter),
                bake(batter),
                bake(batter)
        );
    }
}
```

最后，我们制作了一批糖，并用它对蛋糕进行糖化：

```java
// concurrent/FrostedCake.java

import java.util.concurrent.*;
import java.util.stream.*;
import onjava.Nap;

final class Frosting {
    private Frosting() {
    }

    static CompletableFuture<Frosting> make() {
        new Nap(0.1);
        return CompletableFuture
                .completedFuture(new Frosting());
    }
}

public class FrostedCake {
    public FrostedCake(Baked baked, Frosting frosting) {
        new Nap(0.1);
    }

    @Override
    public String toString() {
        return "FrostedCake";
    }

    public static void main(String[] args) {
        Baked.batch().forEach(
                baked -> baked
                        .thenCombineAsync(Frosting.make(),
                                (cake, frosting) ->
                                        new FrostedCake(cake, frosting))
                        .thenAcceptAsync(System.out::println)
                        .join());
    }
}
```

一旦你习惯了这种背后的想法, `CompletableFuture` 它们相对易于使用。

### 异常

与 `CompletableFuture` 在处理链中包装对象的方式相同，它也会缓冲异常。这些在处理时调用者是无感的，但仅当你尝试提取结果时才会被告知。为了说明它们是如何工作的，我们首先创建一个类，它在特定的条件下抛出一个异常:

```java
// concurrent/Breakable.java
import java.util.concurrent.*;
public class Breakable {
    String id;
    private int failcount;

    public Breakable(String id, int failcount) {
        this.id = id;
        this.failcount = failcount;
    }

    @Override
    public String toString() {
        return "Breakable_" + id + " [" + failcount + "]";
    }

    public static Breakable work(Breakable b) {
        if (--b.failcount == 0) {
            System.out.println(
                    "Throwing Exception for " + b.id + ""
            );
            throw new RuntimeException(
                    "Breakable_" + b.id + " failed"
            );
        }
        System.out.println(b);
        return b;
    }
}
```

当`failcount` > 0，且每次将对象传递给 `work()` 方法时， `failcount - 1` 。当`failcount - 1 = 0` 时，`work()` 将抛出一个异常。如果传给 `work()` 的 `failcount = 0` ，`work()` 永远不会抛出异常。

注意，异常信息此示例中被抛出（ `RuntimeException` )

在下面示例  `test()` 方法中，`work()` 多次应用于 `Breakable`，因此如果 `failcount` 在范围内，就会抛出异常。然而，在测试`A`到`E`中，你可以从输出中看到抛出了异常，但它们从未出现:

```java
// concurrent/CompletableExceptions.java
import java.util.concurrent.*;
public class CompletableExceptions {
    static CompletableFuture<Breakable> test(String id, int failcount) {
        return CompletableFuture.completedFuture(
                new Breakable(id, failcount))
                .thenApply(Breakable::work)
                .thenApply(Breakable::work)
                .thenApply(Breakable::work)
                .thenApply(Breakable::work);
    }

    public static void main(String[] args) {
        // Exceptions don't appear ...
        test("A", 1);
        test("B", 2);
        test("C", 3);
        test("D", 4);
        test("E", 5);
        // ... until you try to fetch the value:
        try {
            test("F", 2).get(); // or join()
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // Test for exceptions:
        System.out.println(
                test("G", 2).isCompletedExceptionally()
        );
        // Counts as "done":
        System.out.println(test("H", 2).isDone());
        // Force an exception:
        CompletableFuture<Integer> cfi =
                new CompletableFuture<>();
        System.out.println("done? " + cfi.isDone());
        cfi.completeExceptionally(
                new RuntimeException("forced"));
        try {
            cfi.get();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
```

输出结果：

```
Throwing Exception for A
Breakable_B [1]
Throwing Exception for B
Breakable_C [2]
Breakable_C [1]
Throwing Exception for C
Breakable_D [3]
Breakable_D [2]
Breakable_D [1]
Throwing Exception for D
Breakable_E [4]
Breakable_E [3]
Breakable_E [2]
Breakable_E [1]
Breakable_F [1]
Throwing Exception for F
java.lang.RuntimeException: Breakable_F failed
Breakable_G [1]
Throwing Exception for G
true
Breakable_H [1]
Throwing Exception for H
true
done? false
java.lang.RuntimeException: forced
```

测试 `A` 到 `E` 运行到抛出异常，然后…并没有将抛出的异常暴露给调用方。只有在测试F中调用 `get()` 时，我们才会看到抛出的异常。
测试 `G` 表明，你可以首先检查在处理期间是否抛出异常，而不抛出该异常。然而，test `H` 告诉我们，不管异常是否成功，它仍然被视为已“完成”。
代码的最后一部分展示了如何将异常插入到 `CompletableFuture` 中，而不管是否存在任何失败。
在连接或获取结果时，我们使用 `CompletableFuture` 提供的更复杂的机制来自动响应异常，而不是使用粗糙的 `try-catch`。
你可以使用与我们看到的所有 `CompletableFuture`  相同的表单来完成此操作:在链中插入一个  `CompletableFuture` 调用。有三个选项 `exceptionally()`，`handle()`， `whenComplete()`:

```java
// concurrent/CatchCompletableExceptions.java
import java.util.concurrent.*;
public class CatchCompletableExceptions {
    static void handleException(int failcount) {
        // Call the Function only if there's an
        // exception, must produce same type as came in:
        CompletableExceptions
                .test("exceptionally", failcount)
                .exceptionally((ex) -> { // Function
                    if (ex == null)
                        System.out.println("I don't get it yet");
                    return new Breakable(ex.getMessage(), 0);
                })
                .thenAccept(str ->
                        System.out.println("result: " + str));

        // Create a new result (recover):
        CompletableExceptions
                .test("handle", failcount)
                .handle((result, fail) -> { // BiFunction
                    if (fail != null)
                        return "Failure recovery object";
                    else
                        return result + " is good";
                })
                .thenAccept(str ->
                        System.out.println("result: " + str));

        // Do something but pass the same result through:
        CompletableExceptions
                .test("whenComplete", failcount)
                .whenComplete((result, fail) -> { // BiConsumer
                    if (fail != null)
                        System.out.println("It failed");
                    else
                        System.out.println(result + " OK");
                })
                .thenAccept(r ->
                        System.out.println("result: " + r));
    }

    public static void main(String[] args) {
        System.out.println("**** Failure Mode ****");
        handleException(2);
        System.out.println("**** Success Mode ****");
        handleException(0);
    }
}
```

输出结果：

```
**** Failure Mode ****
Breakable_exceptionally [1]
Throwing Exception for exceptionally
result: Breakable_java.lang.RuntimeException:
Breakable_exceptionally failed [0]
Breakable_handle [1]
Throwing Exception for handle
result: Failure recovery object
Breakable_whenComplete [1]
Throwing Exception for whenComplete
It failed
**** Success Mode ****
Breakable_exceptionally [-1]
Breakable_exceptionally [-2]
Breakable_exceptionally [-3]
Breakable_exceptionally [-4]
result: Breakable_exceptionally [-4]
Breakable_handle [-1]
Breakable_handle [-2]
Breakable_handle [-3]
Breakable_handle [-4]
result: Breakable_handle [-4] is good
Breakable_whenComplete [-1]
Breakable_whenComplete [-2]
Breakable_whenComplete [-3]
Breakable_whenComplete [-4]
Breakable_whenComplete [-4] OK
result: Breakable_whenComplete [-4]
```

- `exceptionally()`  参数仅在出现异常时才运行。`exceptionally()`  局限性在于，该函数只能返回输入类型相同的值。

- `exceptionally()` 通过将一个好的对象插入到流中来恢复到一个可行的状态。

- `handle()` 一致被调用来查看是否发生异常（必须检查fail是否为true）。

  - 但是 `handle()` 可以生成任何新类型，所以它允许执行处理，而不是像使用 `exceptionally()`那样简单地恢复。

  - `whenComplete()` 类似于handle()，同样必须测试它是否失败，但是参数是一个消费者，并且不修改传递给它的结果对象。


### 流异常（Stream Exception）

通过修改**CompletableExceptions.java**，看看 **CompletableFuture**异常与流异常有何不同：

```java
// concurrent/StreamExceptions.java
import java.util.concurrent.*;
import java.util.stream.*;
public class StreamExceptions {
    
    static Stream<Breakable> test(String id, int failcount) {
        return Stream.of(new Breakable(id, failcount))
                .map(Breakable::work)
                .map(Breakable::work)
                .map(Breakable::work)
                .map(Breakable::work);
    }

    public static void main(String[] args) {
        // No operations are even applied ...
        test("A", 1);
        test("B", 2);
        Stream<Breakable> c = test("C", 3);
        test("D", 4);
        test("E", 5);
        // ... until there's a terminal operation:
        System.out.println("Entering try");
        try {
            c.forEach(System.out::println);   // [1]
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
```

输出结果：

```
Entering try
Breakable_C [2]
Breakable_C [1]
Throwing Exception for C
Breakable_C failed
```

使用 `CompletableFuture`，我们可以看到测试A到E的进展，但是使用流，在你应用一个终端操作之前（e.g. `forEach()`），什么都不会暴露给 Client 

`CompletableFuture` 执行工作并捕获任何异常供以后检索。比较这两者并不容易，因为 `Stream` 在没有终端操作的情况下根本不做任何事情——但是流绝对不会存储它的异常。

### 检查性异常

`CompletableFuture` 和 `parallel Stream` 都不支持包含检查性异常的操作。相反，你必须在调用操作时处理检查到的异常，这会产生不太优雅的代码：

```java
// concurrent/ThrowsChecked.java
import java.util.stream.*;
import java.util.concurrent.*;

public class ThrowsChecked {
    class Checked extends Exception {}

    static ThrowsChecked nochecked(ThrowsChecked tc) {
        return tc;
    }

    static ThrowsChecked withchecked(ThrowsChecked tc) throws Checked {
        return tc;
    }

    static void testStream() {
        Stream.of(new ThrowsChecked())
                .map(ThrowsChecked::nochecked)
                // .map(ThrowsChecked::withchecked); // [1]
                .map(
                        tc -> {
                            try {
                                return withchecked(tc);
                            } catch (Checked e) {
                                throw new RuntimeException(e);
                            }
                        });
    }

    static void testCompletableFuture() {
        CompletableFuture
                .completedFuture(new ThrowsChecked())
                .thenApply(ThrowsChecked::nochecked)
                // .thenApply(ThrowsChecked::withchecked); // [2]
                .thenApply(
                        tc -> {
                            try {
                                return withchecked(tc);
                            } catch (Checked e) {
                                throw new RuntimeException(e);
                            }
                        });
    }
}
```

如果你试图像使用 `nochecked()` 那样使用` withchecked()` 的方法引用，编译器会在 `[1]` 和 `[2]` 中报错。相反，你必须写出lambda表达式(或者编写一个不会抛出异常的包装器方法)。

## 死锁

由于任务可以被阻塞，因此一个任务有可能卡在等待另一个任务上，而后者又在等待别的任务，这样一直下去，知道这个链条上的任务又在等待第一个任务释放锁。这得到了一个任务之间相互等待的连续循环， 没有哪个线程能继续， 这称之为死锁[^6]
如果你运行一个程序，而它马上就死锁了， 你可以立即跟踪下去。真正的问题在于，程序看起来工作良好， 但是具有潜在的死锁危险。这时， 死锁可能发生，而事先却没有任何征兆， 所以 `bug` 会潜伏在你的程序例，直到客户发现它出乎意料的发生（以一种几乎肯定是很难重现的方式发生）。因此在编写并发程序的时候，进行仔细的程序设计以防止死锁是关键部分。
埃德斯·迪克斯特拉（`Essger Dijkstra`）发明的“哲学家进餐"问题是经典的死锁例证。基本描述指定了五位哲学家（此处显示的示例允许任何数目）。这些哲学家将花部分时间思考，花部分时间就餐。他们在思考的时候并不需要任何共享资源；但是他们使用的餐具数量有限。在最初的问题描述中，餐具是叉子，需要两个叉子才能从桌子中间的碗里取出意大利面。常见的版本是使用筷子， 显然，每个哲学家都需要两根筷子才能吃饭。
引入了一个困难：作为哲学家，他们的钱很少，所以他们只能买五根筷子（更一般地讲，筷子的数量与哲学家相同）。他们围在桌子周围，每人之间放一根筷子。 当一个哲学家要就餐时，该哲学家必须同时持有左边和右边的筷子。如果任一侧的哲学家都在使用所需的筷子，则我们的哲学家必须等待，直到可得到必须的筷子。

**StickHolder** 类通过将单根筷子保持在大小为1的**BlockingQueue**中来管理它。**BlockingQueue**是一个设计用于在并发程序中安全使用的集合，如果你调用take()并且队列为空，则它将阻塞（等待）。将新元素放入队列后，将释放该块并返回该值：

```java
// concurrent/StickHolder.java
import java.util.concurrent.*;
public class StickHolder {
    private static class Chopstick {
    }

    private Chopstick stick = new Chopstick();
    private BlockingQueue<Chopstick> holder =
            new ArrayBlockingQueue<>(1);

    public StickHolder() {
        putDown();
    }

    public void pickUp() {
        try {
            holder.take(); // Blocks if unavailable
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void putDown() {
        try {
            holder.put(stick);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

为简单起见，`Chopstick`(`static`) 实际上不是由 `StickHolder` 生产的，而是在其类中保持私有的。

如果您调用了`pickUp()`，而 `stick` 不可用，那么`pickUp()`将阻塞该 `stick`，直到另一个哲学家调用`putDown()` 将 `stick` 返回。 

注意，该类中的所有线程安全都是通过 `BlockingQueue` 实现的。

每个哲学家都是一项任务，他们试图把筷子分别 `pickUp()` 在左手和右手上，这样筷子才能吃东西，然后通过 `putDown()` 放下 `stick`。

```java
// concurrent/Philosopher.java
public class Philosopher implements Runnable {
    private final int seat;
    private final StickHolder left, right;

    public Philosopher(int seat, StickHolder left, StickHolder right) {
        this.seat = seat;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "P" + seat;
    }

    @Override
    public void run() {
        while (true) {
            // System.out.println("Thinking");   // [1]
            right.pickUp();
            left.pickUp();
            System.out.println(this + " eating");
            right.putDown();
            left.putDown();
        }
    }
}
```

没有两个哲学家可以同时成功调用take()同一只筷子。另外，如果一个哲学家已经拿过筷子，那么下一个试图拿起同一根筷子的哲学家将阻塞，等待其被释放。

结果是一个看似无辜的程序陷入了死锁。我在这里使用数组而不是集合，只是因为这种语法更简洁：

```java
// concurrent/DiningPhilosophers.java
// Hidden deadlock
// {ExcludeFromGradle} Gradle has trouble
import java.util.*;
import java.util.concurrent.*;
import onjava.Nap;

public class DiningPhilosophers {
    private StickHolder[] sticks;
    private Philosopher[] philosophers;

    public DiningPhilosophers(int n) {
        sticks = new StickHolder[n];
        Arrays.setAll(sticks, i -> new StickHolder());
        philosophers = new Philosopher[n];
        Arrays.setAll(philosophers, i ->
                new Philosopher(i,
                        sticks[i], sticks[(i + 1) % n]));    // [1]
        // Fix by reversing stick order for this one:
        // philosophers[1] =                     // [2]
        //   new Philosopher(0, sticks[0], sticks[1]);
        Arrays.stream(philosophers)
                .forEach(CompletableFuture::runAsync); // [3]
    }

    public static void main(String[] args) {
        // Returns right away:
        new DiningPhilosophers(5);               // [4]
        // Keeps main() from exiting:
        new Nap(3, "Shutdown");
    }
}
```

- 当你停止查看输出时，该程序将死锁。但是，根据你的计算机配置，你可能不会看到死锁。看来这取决于计算机上的内核数[^7]。两个核心不会产生死锁，但两核以上却很容易产生死锁。
- 此行为使该示例更好地说明了死锁，因为你可能正在具有2核的计算机上编写程序（如果确实是导致问题的原因），并且确信该程序可以正常工作，只能启动它将其安装在另一台计算机上时出现死锁。请注意，不能因为你没或不容易看到死锁，这并不意味着此程序不会在2核机器上发生死锁。 该程序仍然有死锁倾向，只是很少发生——可以说是最糟糕的情况，因为问题不容易出现。
- 在 `DiningPhilosophers` 的构造方法中，每个哲学家都获得一个左右筷子的引用。除最后一个哲学家外，都是通过把哲学家放在下一双空闲筷子之间来初始化： 
  - 最后一位哲学家得到了第0根筷子作为他的右筷子，所以圆桌就完成。
  - 那是因为最后一位哲学家正坐在第一个哲学家的旁边，而且他们俩都共用零筷子。[1]显示了以n为模数选择的右筷子，将最后一个哲学家绕到第一个哲学家的旁边。
- 现在，所有哲学家都可以尝试吃饭，每个哲学家都在旁边等待哲学家放下筷子。
  - 为了让每个哲学家在[3]上运行，调用 `runAsync()`，这意味着DiningPhilosophers的构造函数立即返回到[4]。
  - 如果没有任何东西阻止 `main()` 完成，程序就会退出，不会做太多事情。
  - `Nap` 对象阻止 `main()` 退出，然后在三秒后强制退出(假设/可能是)死锁程序。
  - 在给定的配置中，哲学家几乎不花时间思考。因此，他们在吃东西的时候都争着用筷子，而且往往很快就会陷入僵局。你可以改变这个:

1. 通过增加[4]的值来添加更多哲学家。

2. 在Philosopher.java中取消注释行[1]。

任一种方法都会减少死锁的可能性，这表明编写并发程序并认为它是安全的危险，因为它似乎“在我的机器上运行正常”。你可以轻松地说服自己该程序没有死锁，即使它不是。这个示例相当有趣，因为它演示了看起来可以正确运行，但实际上会可能发生死锁的程序。

要修正死锁问题，你必须明白，当以下四个条件同时满足时，就会发生死锁：

1) 互斥条件。任务使用的资源中至少有一个不能共享的。 这里，一根筷子一次就只能被一个哲学家使用。
2) 至少有一个任务它必须持有一个资源且正在等待获取一个被当前别的任务持有的资源。也就是说，要发生死锁，哲学家必须拿着一根筷子并且等待另一根。
3) 资源不能被任务抢占， 任务必须把资源释放当作普通事件。哲学家很有礼貌，他们不会从其它哲学家那里抢筷子。
4) 必须有循环等待， 这时，一个任务等待其它任务所持有的资源， 后者又在等待另一个任务所持有的资源， 这样一直下去，知道有一个任务在等待第一个任务所持有的资源， 使得大家都被锁住。 在 `DiningPhilosophers.java` 中， 因为每个哲学家都试图先得到右边的 筷子, 然后得到左边的 筷子, 所以发生了循环等待。

因为必须满足所有条件才能导致死锁，所以要阻止死锁的话，只需要破坏其中一个即可。在此程序中，防止死锁的一种简单方法是打破第四个条件。之所以会发生这种情况，是因为每个哲学家都尝试按照特定的顺序拾起自己的筷子：先右后左。因此，每个哲学家都有可能在等待左手的同时握住右手的筷子，从而导致循环等待状态。但是，如果其中一位哲学家尝试首先拿起左筷子，则该哲学家决不会阻止紧邻右方的哲学家拿起筷子，从而排除了循环等待。

在**DiningPhilosophers.java**中，取消注释[1]和其后的一行。这将原来的哲学家[1]替换为筷子颠倒的哲学家。通过确保第二位哲学家拾起并在右手之前放下左筷子，我们消除了死锁的可能性。
这只是解决问题的一种方法。你也可以通过防止其他情况之一来解决它。
没有语言支持可以帮助防止死锁；你有责任通过精心设计来避免这种情况。对于试图调试死锁程序的人来说，这些都不是安慰。当然，避免并发问题的最简单，最好的方法是永远不要共享资源-不幸的是，这并不总是可能的。



## 构造方法非线程安全

当你在脑子里想象一个对象构造的过程，你会很容易认为这个过程是线程安全的。毕竟，在对象初始化完成前对外不可见，所以又怎会对此产生争议呢？确实，[Java 语言规范](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.8.3) (JLS)自信满满地陈述道：“*没必要使构造器的线程同步，因为它会锁定正在构造的对象，直到构造器完成初始化后才对其他线程可见。*”

不幸的是，对象的构造过程如其他操作一样，也会受到共享内存并发问题的影响，只是作用机制可能更微妙罢了。

设想下使用一个 **static** 字段为每个对象自动创建唯一标识符的过程。为了测试其不同的实现过程，我们从一个接口开始。代码示例：

```java
//concurrent/HasID.java
public interface HasID {
    int getID();
}
```

然后 **StaticIDField** 类显式地实现该接口。代码示例：

```java
// concurrent/StaticIDField.java
public class StaticIDField implements HasID {
    private static int counter = 0;
    private int id = counter++;
    public int getID() { return id; }
}
```

如你所想，该类是个简单无害的类，它甚至都没一个显式的构造器来引发问题。当我们运行多个用于创建此类对象的线程时，究竟会发生什么？为了搞清楚这点，我们做了以下测试。代码示例：

```java
// concurrent/IDChecker.java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.concurrent.*;
import com.google.common.collect.Sets;
public class IDChecker {
    public static final int SIZE = 100_000;

    static class MakeObjects implements
        Supplier<List<Integer>> {
        private Supplier<HasID> gen;

        MakeObjects(Supplier<HasID> gen) {
            this.gen = gen;
        }

        @Override public List<Integer> get() {
            return Stream.generate(gen)
            .limit(SIZE)
            .map(HasID::getID)
            .collect(Collectors.toList());
        }
    }

    public static void test(Supplier<HasID> gen) {
        CompletableFuture<List<Integer>>
        groupA = CompletableFuture.supplyAsync(new
            MakeObjects(gen)),
        groupB = CompletableFuture.supplyAsync(new
            MakeObjects(gen));

        groupA.thenAcceptBoth(groupB, (a, b) -> {
            System.out.println(
                Sets.intersection(
                Sets.newHashSet(a),
                Sets.newHashSet(b)).size());
            }).join();
    }
}
```

**MakeObjects** 类是一个生产者类，包含一个能够产生 List\<Integer> 类型的列表对象的 `get()` 方法。通过从每个 `HasID` 对象提取 `ID` 并放入列表中来生成这个列表对象，而 `test()` 方法则创建了两个并行的 **CompletableFuture** 对象，用于运行 **MakeObjects** 生产者类，然后获取运行结果。

使用 Guava 库中的 **Sets.`intersection()` 方法，计算出这两个返回的 List\<Integer> 对象中有多少相同的 `ID`（使用谷歌 Guava 库里的方法比使用官方的 `retainAll()` 方法速度快得多）。

现在我们可以测试上面的 **StaticIDField** 类了。代码示例：

```java
// concurrent/TestStaticIDField.java
public class TestStaticIDField {

    public static void main(String[] args) {
        IDChecker.test(StaticIDField::new);
    }
}
```

输出结果：

```
    13287
```

结果中出现了很多重复项。很显然，纯静态 `int` 用于构造过程并不是线程安全的。让我们使用 **AtomicInteger** 来使其变为线程安全的。代码示例：

```java
// concurrent/GuardedIDField.java
import java.util.concurrent.atomic.*;
public class GuardedIDField implements HasID {  
    private static AtomicInteger counter = new
        AtomicInteger();

    private int id = counter.getAndIncrement();

    public int getID() { return id; }

    public static void main(String[] args) {                IDChecker.test(GuardedIDField::new);
    }
}
```

输出结果：

```
    0
```

构造器有一种更微妙的状态共享方式：通过构造器参数：

```java
// concurrent/SharedConstructorArgument.java
import java.util.concurrent.atomic.*;
interface SharedArg{
    int get();
}

class Unsafe implements SharedArg{
    private int i = 0;

    public int get(){
        return i++;
    }
}

class Safe implements SharedArg{
    private static AtomicInteger counter = new AtomicInteger();

    public int get(){
        return counter.getAndIncrement();
    }
}

class SharedUser implements HasID{
    private final int id;

    SharedUser(SharedArg sa){
        id = sa.get();
    }

    @Override
    public int getID(){
        return id;
    }
}

public class SharedConstructorArgument{
    public static void main(String[] args){
        Unsafe unsafe = new Unsafe();
        IDChecker.test(() -> new SharedUser(unsafe));

        Safe safe = new Safe();
        IDChecker.test(() -> new SharedUser(safe));
    }
}
```

输出结果：

```
    24838
    0
```

在这里，**SharedUser** 构造器实际上共享了相同的参数。即使 **SharedUser** 以完全无害且合理的方式使用其自己的参数，其构造器的调用方式也会引起冲突。**SharedUser** 甚至不知道它是以这种方式调用的，更不必说控制它了。

同步构造器并不被java语言所支持，但是通过使用同步语块来创建你自己的同步构造器是可能的（请参阅附录：[并发底层原理](./Appendix-Low-Level-Concurrency.md)，来进一步了解同步关键字—— `synchronized`）。尽管JLS（java语言规范）这样陈述道：“……它会锁定正在构造的对象”，但这并不是真的——构造器实际上只是一个静态方法，因此同步构造器实际上会锁定该类的Class对象。我们可以通过创建自己的静态对象并锁定它，来达到与同步构造器相同的效果：

```java
// concurrent/SynchronizedConstructor.java

import java.util.concurrent.atomic.*;

class SyncConstructor implements HasID{
    private final int id;
    private static Object constructorLock =
        new Object();

    SyncConstructor(SharedArg sa){
        synchronized (constructorLock){
            id = sa.get();
        }
    }

    @Override
    public int getID(){
        return id;
    }
}

public class SynchronizedConstructor{
    public static void main(String[] args){
        Unsafe unsafe = new Unsafe();
        IDChecker.test(() -> new SyncConstructor(unsafe));
    }
}
```

输出结果：

```
    0
```

**Unsafe**类的共享使用现在就变得安全了。另一种方法是将构造器设为私有（因此可以防止继承），并提供一个静态Factory方法来生成新对象：

```java
// concurrent/SynchronizedFactory.java
import java.util.concurrent.atomic.*;

final class SyncFactory implements HasID{
    private final int id;

    private SyncFactory(SharedArg sa){
        id = sa.get();
    }

    @Override
    public int getID(){
        return id;
    }

    public static synchronized SyncFactory factory(SharedArg sa){
        return new SyncFactory(sa);
    }
}

public class SynchronizedFactory{
    public static void main(String[] args){
        Unsafe unsafe = new Unsafe();
        IDChecker.test(() -> SyncFactory.factory(unsafe));
    }
}
```

输出结果：

```
    0
```

通过同步静态工厂方法，可以在构造过程中锁定 **Class** 对象。

这些示例充分表明了在并发Java程序中检测和管理共享状态有多困难。即使你采取“不共享任何内容”的策略，也很容易产生意外的共享事件。

## 复杂性和代价

假设你正在做披萨，我们把从整个流程的当前步骤到下一个步骤所需的工作量，在这里一一表示为枚举变量的一部分：

```java
// concurrent/Pizza.java import java.util.function.*;

import onjava.Nap;
public class Pizza{
    public enum Step{
        DOUGH(4), ROLLED(1), SAUCED(1), CHEESED(2),
        TOPPED(5), BAKED(2), SLICED(1), BOXED(0);
        int effort;// Needed to get to the next step 

        Step(int effort){
            this.effort = effort;
        }

        Step forward(){
            if (equals(BOXED)) return BOXED;
            new Nap(effort * 0.1);
            return values()[ordinal() + 1];
        }
    }

    private Step step = Step.DOUGH;
    private final int id;

    public Pizza(int id){
        this.id = id;
    }

    public Pizza next(){
        step = step.forward();
        System.out.println("Pizza " + id + ": " + step);
        return this;
    }

    public Pizza next(Step previousStep){
        if (!step.equals(previousStep))
            throw new IllegalStateException("Expected " +
                      previousStep + " but found " + step);
        return next();
    }

    public Pizza roll(){
        return next(Step.DOUGH);
    }

    public Pizza sauce(){
        return next(Step.ROLLED);
    }

    public Pizza cheese(){
        return next(Step.SAUCED);
    }

    public Pizza toppings(){
        return next(Step.CHEESED);
    }

    public Pizza bake(){
        return next(Step.TOPPED);
    }

    public Pizza slice(){
        return next(Step.BAKED);
    }

    public Pizza box(){
        return next(Step.SLICED);
    }

    public boolean complete(){
        return step.equals(Step.BOXED);
    }

    @Override
    public String toString(){
        return "Pizza" + id + ": " + (step.equals(Step.BOXED) ? "complete" : step);
    }
}
```

这只算得上是一个平凡的状态机，就像**Machina**类一样。 

制作一个披萨，当披萨饼最终被放在盒子中时，就算完成最终任务了。 如果一个人在做一个披萨饼，那么所有步骤都是线性进行的，即一个接一个地进行：

```java
// concurrent/OnePizza.java 

import onjava.Timer;

public class OnePizza{
    public static void main(String[] args){
        Pizza za = new Pizza(0);
        System.out.println(Timer.duration(() -> {
            while (!za.complete()) za.next();
        }));
    }
}
```

输出结果：

```
Pizza 0: ROLLED 
Pizza 0: SAUCED 
Pizza 0: CHEESED 
Pizza 0: TOPPED 
Pizza 0: BAKED 
Pizza 0: SLICED 
Pizza 0: BOXED 
	1622 
```

时间以毫秒为单位，加总所有步骤的工作量，会得出与我们的期望值相符的数字。 如果你以这种方式制作了五个披萨，那么你会认为它花费的时间是原来的五倍。 但是，如果这还不够快怎么办？ 我们可以从尝试并行流方法开始：

```java
// concurrent/PizzaStreams.java
// import java.util.*; import java.util.stream.*;

import onjava.Timer;

public class PizzaStreams{
    static final int QUANTITY = 5;

    public static void main(String[] args){
        Timer timer = new Timer();
        IntStream.range(0, QUANTITY)
            .mapToObj(Pizza::new)
            .parallel()//[1]
        	.forEach(za -> { while(!za.complete()) za.next(); }); 			System.out.println(timer.duration());
    }
}
```

输出结果：

```
Pizza 2: ROLLED
Pizza 0: ROLLED
Pizza 1: ROLLED
Pizza 4: ROLLED
Pizza 3:ROLLED
Pizza 2:SAUCED
Pizza 1:SAUCED
Pizza 0:SAUCED
Pizza 4:SAUCED
Pizza 3:SAUCED
Pizza 2:CHEESED
Pizza 1:CHEESED
Pizza 0:CHEESED
Pizza 4:CHEESED
Pizza 3:CHEESED
Pizza 2:TOPPED
Pizza 1:TOPPED
Pizza 0:TOPPED
Pizza 4:TOPPED
Pizza 3:TOPPED
Pizza 2:BAKED
Pizza 1:BAKED
Pizza 0:BAKED
Pizza 4:BAKED
Pizza 3:BAKED
Pizza 2:SLICED
Pizza 1:SLICED
Pizza 0:SLICED
Pizza 4:SLICED
Pizza 3:SLICED
Pizza 2:BOXED
Pizza 1:BOXED
Pizza 0:BOXED
Pizza 4:BOXED
Pizza 3:BOXED
1739
```

现在，我们制作五个披萨的时间与制作单个披萨的时间就差不多了。 尝试删除标记为[1]的行后，你会发现它花费的时间是原来的五倍。 你还可以尝试将**QUANTITY**更改为4、8、10、16和17，看看会有什么不同，并猜猜看为什么会这样。

**PizzaStreams** 类产生的每个并行流在它的`forEach()`内完成所有工作，如果我们将其各个步骤用映射的方式一步一步处理，情况会有所不同吗？

```java
// concurrent/PizzaParallelSteps.java 

import java.util.*;
import java.util.stream.*;
import onjava.Timer;

public class PizzaParallelSteps{
    static final int QUANTITY = 5;

    public static void main(String[] args){
        Timer timer = new Timer();
        IntStream.range(0, QUANTITY)
            .mapToObj(Pizza::new)
            .parallel()
            .map(Pizza::roll)
            .map(Pizza::sauce)
            .map(Pizza::cheese)
            .map(Pizza::toppings)
            .map(Pizza::bake)
            .map(Pizza::slice)
            .map(Pizza::box)
            .forEach(za -> System.out.println(za));
        System.out.println(timer.duration());
    }
} 
```

输出结果：

```
Pizza 2: ROLLED 
Pizza 0: ROLLED 
Pizza 1: ROLLED 
Pizza 4: ROLLED 
Pizza 3: ROLLED 
Pizza 1: SAUCED 
Pizza 0: SAUCED 
Pizza 2: SAUCED 
Pizza 3: SAUCED 
Pizza 4: SAUCED 
Pizza 1: CHEESED 
Pizza 0: CHEESED 
Pizza 2: CHEESED 
Pizza 3: CHEESED 
Pizza 4: CHEESED 
Pizza 0: TOPPED 
Pizza 2: TOPPED
Pizza 1: TOPPED 
Pizza 3: TOPPED 
Pizza 4: TOPPED 
Pizza 1: BAKED 
Pizza 2: BAKED 
Pizza 0: BAKED 
Pizza 4: BAKED 
Pizza 3: BAKED 
Pizza 0: SLICED 
Pizza 2: SLICED 
Pizza 1: SLICED 
Pizza 3: SLICED 
Pizza 4: SLICED 
Pizza 1: BOXED 
Pizza1: complete 
Pizza 2: BOXED 
Pizza 0: BOXED 
Pizza2: complete 
Pizza0: complete 
Pizza 3: BOXED
Pizza 4: BOXED 
Pizza4: complete 
Pizza3: complete 
1738 
```

答案是“否”，事后看来这并不奇怪，因为每个披萨都需要按顺序执行步骤。因此，没法通过分步执行操作来进一步提高速度，就像上文的 `PizzaParallelSteps.java` 里面展示的一样。

我们可以使用 **CompletableFutures** 重写这个例子：

```java
// concurrent/CompletablePizza.java 

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import onjava.Timer;

public class CompletablePizza{
    static final int QUANTITY = 5;

    public static CompletableFuture<Pizza> makeCF(Pizza za){
        return CompletableFuture
                .completedFuture(za)
            .thenApplyAsync(Pizza::roll)
            .thenApplyAsync(Pizza::sauce)
            .thenApplyAsync(Pizza::cheese)
            .thenApplyAsync(Pizza::toppings)
            .thenApplyAsync(Pizza::bake)
            .thenApplyAsync(Pizza::slice)
            .thenApplyAsync(Pizza::box);
    }

    public static void show(CompletableFuture<Pizza> cf){
        try{
            System.out.println(cf.get());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
        Timer timer = new Timer();
        List<CompletableFuture<Pizza>> pizzas =
                IntStream.range(0, QUANTITY)
            .mapToObj(Pizza::new)
            .map(CompletablePizza::makeCF)
            .collect(Collectors.toList());
        System.out.println(timer.duration());
        pizzas.forEach(CompletablePizza::show);
        System.out.println(timer.duration());
    }
}
```

输出结果：

```
169 
Pizza 0: ROLLED 
Pizza 1: ROLLED 
Pizza 2: ROLLED 
Pizza 4: ROLLED 
Pizza 3: ROLLED 
Pizza 1: SAUCED 
Pizza 0: SAUCED 
Pizza 2: SAUCED 
Pizza 4: SAUCED
Pizza 3: SAUCED 
Pizza 0: CHEESED 
Pizza 4: CHEESED 
Pizza 1: CHEESED 
Pizza 2: CHEESED 
Pizza 3: CHEESED 
Pizza 0: TOPPED 
Pizza 4: TOPPED 
Pizza 1: TOPPED 
Pizza 2: TOPPED 
Pizza 3: TOPPED 
Pizza 0: BAKED 
Pizza 4: BAKED 
Pizza 1: BAKED 
Pizza 3: BAKED 
Pizza 2: BAKED 
Pizza 0: SLICED 
Pizza 4: SLICED 
Pizza 1: SLICED 
Pizza 3: SLICED
Pizza 2: SLICED 
Pizza 4: BOXED 
Pizza 0: BOXED 
Pizza0: complete 
Pizza 1: BOXED 
Pizza1: complete 
Pizza 3: BOXED 
Pizza 2: BOXED 
Pizza2: complete 
Pizza3: complete 
Pizza4: complete 
1797 
```

并行流和 **CompletableFutures** 是 Java 并发工具箱中最先进发达的技术。 你应该始终首先选择其中之一。 当一个问题很容易并行处理时，或者说，很容易把数据分解成相同的、易于处理的各个部分时，使用并行流方法处理最为合适（而如果你决定不借助它而由自己完成，你就必须撸起袖子，深入研究**Spliterator**的文档）。

而当工作的各个部分内容各不相同时，使用 **CompletableFutures** 是最好的选择。比起面向数据，**CompletableFutures** 更像是面向任务的。

对于披萨问题，结果似乎也没有什么不同。实际上，并行流方法看起来更简洁，仅出于这个原因，我认为并行流作为解决问题的首次尝试方法更具吸引力。

由于制作披萨总需要一定的时间，无论你使用哪种并发方法，你能做到的最好情况，是在制作一个披萨的相同时间内制作n个披萨。 在这里当然很容易看出来，但是当你处理更复杂的问题时，你就可能忘记这一点。 通常，在项目开始时进行粗略的计算，就能很快弄清楚最大可能的并行吞吐量，这可以防止你因为采取无用的加快运行速度的举措而忙得团团转。

使用 **CompletableFutures** 或许可以轻易地带来重大收益，但是在尝试更进一步时需要倍加小心，因为额外增加的成本和工作量会非常容易远远超出你之前拼命挤出的那一点点收益。

## 本章小结

需要并发的唯一理由是“等待太多”。这也可以包括用户界面的响应速度，但是由于Java用于构建用户界面时并不高效，因此[^8]这仅仅意味着“你的程序运行速度还不够快”。

如果并发很容易，则没有理由拒绝并发。 正因为并发实际上很难，所以你应该仔细考虑是否值得为此付出努力，并考虑你能否以其他方式提升速度。

例如，迁移到更快的硬件（这可能比消耗程序员的时间要便宜得多）或者将程序分解成多个部分，然后在不同的机器上运行这些部分。

奥卡姆剃刀是一个经常被误解的原则。 我看过至少一部电影，他们将其定义为”最简单的解决方案是正确的解决方案“，就好像这是某种毋庸置疑的法律。实际上，这是一个准则：面对多种方法时，请先尝试需要最少假设的方法。 在编程世界中，这已演变为“尝试可能可行的最简单的方法”。当你了解了特定工具的知识时——就像你现在了解了有关并发性的知识一样，你可能会很想使用它，或者提前规定你的解决方案必须能够“速度飞快”，从而来证明从一开始就进行并发设计是合理的。但是，我们的奥卡姆剃刀编程版本表示你应该首先尝试最简单的方法（这种方法开发起来也更便宜），然后看看它是否足够好。

由于我出身于底层学术背景（物理学和计算机工程），所以我很容易想到所有小轮子转动的成本。我确定使用最简单的方法不够快的场景出现的次数已经数不过来了，但是尝试后却发现它实际上绰绰有余。

### 缺点

并发编程的主要缺点是：

1. 在线程等待共享资源时会降低速度。 

2. 线程管理产生额外CPU开销。

3. 糟糕的设计决策带来无法弥补的复杂性。

4. 诸如饥饿，竞速，死锁和活锁（多线程各自处理单个任务而整体却无法完成）之类的问题。

5. 跨平台的不一致。 通过一些示例，我发现了某些计算机上很快出现的竞争状况，而在其他计算机上却没有。 如果你在后者上开发程序，则在分发程序时可能会感到非常惊讶。

另外，并发的应用是一门艺术。 Java旨在允许你创建尽可能多的所需要的对象来解决问题——至少在理论上是这样。[^9]但是，线程不是典型的对象：每个线程都有其自己的执行环境，包括堆栈和其他必要的元素，使其比普通对象大得多。 在大多数环境中，只能在内存用光之前创建数千个**Thread**对象。通常，你只需要几个线程即可解决问题，因此一般来说创建线程没有什么限制，但是对于某些设计而言，它会成为一种约束，可能迫使你使用完全不同的方案。

### 共享内存陷阱

并发性的主要困难之一是因为可能有多个任务共享一个资源（例如对象中的内存），并且你必须确保多个任务不会同时读取和更改该资源。

我花了多年的时间研究并发并发。 我了解到你永远无法相信使用共享内存并发的程序可以正常工作。 你可以轻易发现它是错误的，但永远无法证明它是正确的。 这是众所周知的并发原则之一。[^10]

我遇到了许多人，他们对编写正确的线程程序的能力充满信心。 我偶尔开始认为我也可以做好。 对于一个特定的程序，我最初是在只有单个CPU的机器上编写的。 那时我能够说服自己该程序是正确的，因为我以为我对Java工具很了解。 而且在我的单CPU计算机上也没有失败。而到了具有多个CPU的计算机，程序出现问题不能运行后，我感到很惊讶，但这还只是众多问题中的一个而已。 这不是Java的错； “写一次，到处运行”，在单核与多核计算机间无法扩展到并发编程领域。这是并发编程的基本问题。 实际上你可以在单CPU机器上发现一些并发问题，但是在多线程实际上真的在并行运行的多CPU机器上，就会出现一些其他问题。

再举一个例子，哲学家就餐的问题可以很容易地进行调整，因此几乎不会产生死锁，这会给你一种一切都棒极了的印象。当涉及到共享内存并发编程时，你永远不应该对自己的编程能力变得过于自信。

### This Albatross is Big

如果你对Java并发感到不知所措，那说明你身处在一家出色的公司里。你可以访问**Thread**类的[Javadoc](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html)页面， 看一下哪些方法现在是**Deprecated**（废弃的）。这些是Java语言设计者犯过错的地方，因为他们在设计语言时对并发性了解不足。

事实证明，在Java的后续版本中添加的许多库解决方案都是无效的，甚至是无用的。 幸运的是，Java 8中的并行**Streams**和**CompletableFutures**都非常有价值。但是当你使用旧代码时，仍然会遇到旧的解决方案。

在本书的其他地方，我谈到了Java的一个基本问题：每个失败的实验都永远嵌入在语言或库中。 Java并发强调了这个问题。尽管有不少错误，但错误并不是那么多，因为有很多不同的尝试方法来解决问题。 好的方面是，这些尝试产生了更好，更简单的设计。 不利之处在于，在找到好的方法之前，你很容易迷失于旧的设计中。

### 其他类库

本章重点介绍了相对安全易用的并行工具流和**CompletableFutures**，并且仅涉及Java标准库中一些更细粒度的工具。 为避免你不知所措，我没有介绍你可能实际在实践中使用的某些库。我们使用了几个**Atomic**（原子）类，**ConcurrentLinkedDeque**，**ExecutorService**和**ArrayBlockingQueue**。附录：[并发底层原理](./Appendix-Low-Level-Concurrency.md)涵盖了其他一些内容，但是你还想探索**java.util.concurrent**的Javadocs。 但是要小心，因为某些库组件已被新的更好的组件所取代。

### 考虑为并发设计的语言

通常，请谨慎地使用并发。 如果需要使用它，请尝试使用最现代的方法：并行流或**CompletableFutures**。 这些功能旨在（假设你不尝试共享内存）使你摆脱麻烦（在Java的世界范围内）。

如果你的并发问题变得比高级Java构造所支持的问题更大且更复杂，请考虑使用专为并发设计的语言，仅在需要并发的程序部分中使用这种语言是有可能的。 在撰写本文时，JVM上最纯粹的功能语言是Clojure（Lisp的一种版本）和Frege（Haskell的一种实现）。这些使你可以在其中编写应用程序的并发部分语言，并通过JVM轻松地与你的主要Java代码进行交互。 或者，你可以选择更复杂的方法，即通过外部功能接口（FFI）将JVM之外的语言与另一种为并发设计的语言进行通信。[^11]

你很容易被一种语言绑定，迫使自己尝试使用该语言来做所有事情。 一个常见的示例是构建HTML / JavaScript用户界面。 这些工具确实很难使用，令人讨厌，并且有许多库允许你通过使用自己喜欢的语言编写代码来生成这些工具（例如，**Scala.js**允许你在Scala中完成代码）。

心理上的便利是一个合理的考虑因素。 但是，我希望我在本章（以及附录：[并发底层原理](./Appendix-Low-Level-Concurrency.md)）中已经表明Java并发是一个你可能无法逃离很深的洞。 与Java语言的任何其他部分相比，在视觉上检查代码同时记住所有陷阱所需要的的知识要困难得多。

无论使用特定的语言、库使得并发看起来多么简单，都要将其视为一种妖术，因为总是有东西会在你最不期望出现的时候咬你。

### 拓展阅读

《Java Concurrency in Practice》，出自Brian Goetz，Tim Peierls， Joshua Bloch，Joseph Bowbeer，David Holmes和 Doug Lea (Addison Wesley，2006年)——这些基本上就是Java并发世界中的名人名单了《Java Concurrency in Practice》第二版，出自 Doug Lea (Addison-Wesley，2000年)。尽管这本书出版时间远远早于Java 5发布，但Doug的大部分工作都写入了**java.util.concurrent**库。因此，这本书对于全面理解并发问题至关重要。 它超越了Java，讨论了跨语言和技术的并发编程。 尽管它在某些地方可能很钝，但值得多次重读（最好是在两个月之间进行消化）。 道格（Doug）是世界上为数不多的真正了解并发编程的人之一，因此这是值得的。

[^1]:例如,Eric-Raymond在“Unix编程艺术”（Addison-Wesley，2004）中提出了一个很好的案例。
[^2]:可以说，试图将并发性用于后续语言是一种注定要失败的方法，但你必须得出自己的结论
[^3]:有人谈论在Java——10中围绕泛型做一些类似的基本改进，这将是非常令人难以置信的。
[^4]:这是一种有趣的，虽然不一致的方法。通常，我们期望在公共接口上使用显式类表示不同的行为
[^5]:不，永远不会有纯粹的功能性Java。我们所能期望的最好的是一种在JVM上运行的全新语言。
[^6]:当两个任务能够更改其状态以使它们不会被阻止但它们从未取得任何有用的进展时，你也可以使用活动锁。
[^7]: 而不是超线程；通常每个内核有两个超线程，并且在询问内核数量时，本书所使用的Java版本会报告超线程的数量。超线程产生了更快的上下文切换，但是只有实际的内核才真的工作，而不是超线程。 ↩
[^8]: 库就在那里用于调用，而语言本身就被设计用于此目的，但实际上它很少发生，以至于可以说”没有“。↩
[^9]: 举例来说，如果没有Flyweight设计模式，在工程中创建数百万个对象用于有限元分析可能在Java中不可行。↩
[^10]: 在科学中，虽然从来没有一种理论被证实过，但是一种理论必须是可证伪的才有意义。而对于并发性，我们大部分时间甚至都无法得到这种可证伪性。↩
[^11]: 尽管**Go**语言显示了FFI的前景，但在撰写本文时，它并未提供跨所有平台的解决方案。

<!-- 分页 -->

<div style="page-break-after: always;"></div>