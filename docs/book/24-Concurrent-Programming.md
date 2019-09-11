[TOC]

<!-- Concurrent Programming -->
# 第二十四章 并发编程

>爱丽丝：“但是我不想进入疯狂的人群众”
>
>猫咪：“oh，你无能为力，我们都疯了，我疯了，你也疯了”
>
>爱丽丝：“你怎么知道我疯了”。
>
>猫咪：“你一定疯了，否则你不会来到这里”——爱丽丝梦游仙境 第6章。

到目前为止，我们一直在编程，就像文学中的意识流叙事设备一样：首先发生一件事，然后是下一件事。我们完全控制所有步骤及其发生的顺序。如果我们将值设置为5，那么稍后会回来并发现它是47，这将是非常令人惊讶的。

我们现在进入了一个奇怪的并发世界，在此这个结果并不令人惊讶。你信赖的一切都不再可靠。它可能有效，也可能没有。很可能它会在某些条件下有效，而不是在其他条件下，你必须知道和了解这些情况以确定哪些有效。

作为类比，你的正常生活是在牛顿力学中发生的。物体具有质量：它们会下降并移动它们的动量。电线具有阻力，过线可以直线传播。但是，如果你进入非常小、热、冷、或者大的世界（我们不能生存），这些事情会发生变化。我们无法判断某物体是粒子还是波，光是否受到重力影响，一些物质变为超导体。

而不是单一的意识流叙事，我们在同时多条故事线进行的间谍小说里。一个间谍在一个特殊的岩石下李璐下微缩胶片，当第二个间谍来取回包裹时，它可能已经被第三个间谍带走了。但是这部特别的小说并没有把事情搞得一团糟;你可以轻松地走到尽头，永远不会弄明白什么。

构建并发应用程序非常类似于游戏[Jenga](https://en.wikipedia.org/wiki/Jenga)，每当你拉出一个块并将其放置在塔上时，一切都会崩溃。每个塔楼和每个应用程序都是独一无二的，有自己的作用。您从构建系统中学到的东西可能不适用于下一个系统。

本章是对并发性的一个非常基本的介绍。虽然我使用了最现代的Java 8工具来演示原理，但这一章远非对该主题的全面处理。我的目标是为你提供足够的基础知识，使你能够解决问题的复杂性和危险性，从而安全的通过这些鲨鱼肆虐的困难水域。

对于更多凌乱，低级别的细节，请参阅附录：[并发底层原理](./Appendix-Low-Level-Concurrency.md)。要进一步深入这个领域，你还必须阅读Brian Goetz等人的Java Concurrency in Practice。虽然在写作时，这本书已有十多年的历史，但它仍然包含你必须了解和理解的必需品。理想情况下，本章和附录是该书的精心准备。另一个有价值的资源是**Bill Venner**的Inside the Java Virtual Machine，它详细描述了JVM的最内部工作方式，包括线程。

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

您可能会认为[纯函数式](https://en.wikipedia.org/wiki/Purely_functional)语言没有这些限制。实际上，纯函数式语言解决了大量并发问题，所以如果你正在解决一个困难的并发问题，你可以考虑用纯函数语言编写这个部分。但最终，如果你编写一个使用队列的系统，例如，如果它没有正确调整并且输入速率要么没有被正确估计或被限制（并且限制意味着,在不同情况下不同的东西具有不同的影响），该队列将填满并阻塞或溢出。最后，您必须了解所有细节，任何问题都可能会破坏您的系统。这是一种非常不同的编程方式

<!-- A New Definition ofConcurrencyFor -->
### 并发的新定义

几十年来，我一直在努力解决各种形式的并发问题，其中一个最大的挑战一直是简单地定义它。在撰写本章的过程中，我终于有了这样的洞察力，我认为可以定义它：
>**并发性是一系列性能技术，专注于减少等待**

这实际上是一个相当多的声明，所以我将其分解：

- 这是一个集合：有许多不同的方法来解决这个问题。这是使定义并发性如此具有挑战性的问题之一，因为技术差别很大
- 这些是性能技术：就是这样。并发的关键点在于让您的程序运行得更快。在Java中，并发是非常棘手和困难的，所以绝对不要使用它，除非你有一个重大的性能问题 - 即使这样，使用最简单的方法产生你需要的性能，因为并发很快变得无法管理。
- “减少等待”部分很重要而且微妙。无论（例如）你运行多少个处理器，你只能在等待某个地方时产生结果。如果您发起I/O请求并立即获得结果，没有延迟，因此无需改进。如果您在多个处理器上运行多个任务，并且每个处理器都以满容量运行，并且任何其他任务都没有等待，那么尝试提高吞吐量是没有意义的。并发的唯一形式是如果程序的某些部分被迫等待。等待可以以多种形式出现 - 这解释了为什么存在如此不同的并发方法。

值得强调的是，这个定义的有效性取决于等待这个词。如果没有什么可以等待，那就没有机会了。如果有什么东西在等待，那么就会有很多方法可以加快速度，这取决于多种因素，包括系统运行的配置，你要解决的问题类型以及其他许多问题。
<!-- Concurrency Superpowers -->
## 并发的超能力

想象一下，你是一部科幻电影。您必须在高层建筑中搜索一个精心巧妙地隐藏在建筑物的一千万个房间之一中的单个物品。您进入建筑物并移动走廊。走廊分开了。

你自己完成这项任务需要一百个生命周期。

现在假设你有一个奇怪的超级大国。你可以将自己分开，然后在继续前进的同时将另一半送到另一个走廊。每当你在走廊或楼梯上遇到分隔到下一层时，你都会重复这个分裂的技巧。最后，你会有一个人在整个建筑物的每个终点走廊。

每个走廊都有一千个房间。你的超级大国正在变得有点瘦，所以你只能让自己50个人同时搜索房间。

一旦克隆体进入房间，它必须搜索房间的所有裂缝和隐藏的口袋。它切换到第二个超级大国。它分成了一百万个纳米机器人，每个机器人都会飞到或爬到房间里一些看不见的地方。你不明白这种力量 - 一旦你启动它就会起作用。在他们自己的控制下，纳米机器人开始行动，搜索房间然后回来重新组装成你，突然，不知何故，你只知道物品是否在房间里

我很想能够说，“你在科幻小说中的超级大国？这就是并发性。“每当你有更多的任务要解决时，它就像分裂两个一样简单。问题是我们用来描述这种现象的任何模型最终都是抽象的

以下是其中一个漏洞：在理想的世界中，每次克隆自己时，您还会复制硬件处理器来运行该克隆。但当然不会发生这种情况 - 您的机器上可能有四个或八个处理器（通常在写入时）。您可能还有更多，并且仍有许多情况只有一个处理器。在抽象的讨论中，物理处理器的分配方式不仅可以泄漏，甚至可以支配您的决策

让我们在科幻电影中改变一些东西。现在当每个克隆搜索者最终到达一扇门时，他们必须敲门并等到有人回答。如果我们每个搜索者有一个处理器，这没有问题 - 处理器只是空闲，直到门被回答。但是如果我们只有8个处理器和数千个搜索者，那么只是因为搜索者恰好是因为处理器闲置了被锁，等待一扇门被接听。相反，我们希望将处理器应用于搜索，在那里它可以做一些真正的工作，因此需要将处理器从一个任务切换到另一个任务的机制。

许多型号能够有效地隐藏处理器的数量，并允许您假装您的数量非常大。但是有些情况会发生故障的时候，你必须知道处理器的数量，以便你可以解决这个问题。

其中一个最大的影响取决于您是单个处理器还是多个处理器。如果你只有一个处理器，那么任务切换的成本也由该处理器承担，将并发技术应用于你的系统会使它运行得更慢。

这可能会让您决定，在单个处理器的情况下，编写并发代码时没有意义。然而，有些情况下，并发模型会产生更简单的代码，实际上值得让它运行得更慢以实现。

在克隆体敲门等待的情况下，即使单处理器系统也能从并发中受益，因为它可以从等待（阻塞）的任务切换到准备好的任务。但是如果所有任务都可以一直运行那么切换的成本会降低一切，在这种情况下，如果你有多个进程，并发通常只会有意义。

在接听电话的客户服务部门，你只有一定数量的人，但是你可以拨打很多电话。那些人（处理器）必须一次拨打一个电话，直到完成电话和额外的电话必须排队。

在“鞋匠和精灵”的童话故事中，鞋匠做了很多工作，当他睡着时，一群精灵来为他制作鞋子。这里的工作是分布式的，但即使使用大量的物理处理器，在制造鞋子的某些部件时会产生限制 - 例如，如果鞋底需要制作鞋子，这会限制制鞋的速度并改变您设计解决方案的方式。

因此，您尝试解决的问题驱动解决方案的设计。打破一个“独立运行”问题的高级[原文：lovely ]抽象，然后就是实际发生的现实。物理现实不断侵入和震撼，这种抽象。

这只是问题的一部分。考虑一个制作蛋糕的工厂。我们不知何故在工人中分发了蛋糕制作任务，但是现在是时候让工人把蛋糕放在盒子里了。那里有一个盒子，准备收到蛋糕。但是，在工人将蛋糕放入盒子之前，另一名工人投入并将蛋糕放入盒子中！我们的工人已经把蛋糕放进去了，然后就开始了！这两个蛋糕被砸碎并毁了。这是常见的“共享内存”问题，产生我们称之为竞争条件的问题，其结果取决于哪个工作人员可以首先在框中获取蛋糕（通常使用锁定机制来解决问题，因此一个工作人员可以先抓住框并防止蛋糕砸）。

当“同时”执行的任务相互干扰时，会出现问题。他可以以如此微妙和偶然的方式发生，可能公平地说，并发性“可以说是确定性的，但实际上是非确定性的。”也就是说，你可以假设编写通过维护和代码检查正常工作的并发程序。然而，在实践中，编写仅看起来可行的并发程序更为常见，但是在适当的条件下，将会失败。这些情况可能会发生，或者很少发生，你在测试期间从未看到它们。实际上，编写测试代码通常无法为并发程序生成故障条件。由此产生的失败只会偶尔发生，因此它们以客户投诉的形式出现。
这是推动并发的最强有力的论据之一：如果你忽略它，你可能会被咬。

因此，并发似乎充满了危险，如果这让你有点害怕，这可能是一件好事。尽管Java 8在并发性方面做出了很大改进，但仍然没有像编译时验证或检查异常那样的安全网来告诉您何时出现错误。通过并发，您可以自己动手，只有知识渊博，可疑和积极，才能用Java编写可靠的并发代码。

<!-- Concurrency is for Speed -->
<!-- 不知道是否可以找到之前翻译的针对速度感觉太直了 -->
## 并发为速度而生

在听说并发编程的问题之后，你可能会想知道它是否值得这么麻烦。答案是“不，除非你的程序运行速度不够快。”并且在决定它没有之前你会想要仔细思考。不要随便跳进并发编程的悲痛之中。如果有一种方法可以在更快的机器上运行您的程序，或者如果您可以对其进行分析并发现瓶颈并在该位置交换更快的算法，那么请执行此操作。只有在显然没有其他选择时才开始使用并发，然后仅在孤立的地方。

速度问题一开始听起来很简单：如果你想要一个程序运行得更快，将其分解成碎片并在一个单独的处理器上运行每个部分。由于我们能够提高时钟速度流（至少对于传统芯片），速度的提高是出现在多核处理器的形式而不是更快的芯片。为了使你的程序运行得更快，你必须学习利用那些超级处理器，这是并发性给你的一个建议。

使用多处理器机器，可以在这些处理器之间分配多个任务，这可以显着提高吞吐量。强大的多处理器Web服务器通常就是这种情况，它可以在程序中为CPU分配大量用户请求，每个请求分配一个线程。

但是，并发性通常可以提高在单个处理器上运行的程序的性能。这听起来像是一个双向的。如果考虑一下，由于上下文切换的成本增加（从一个任务更改为另一个任务），在单个处理器上运行的并发程序实际上应该比程序的所有部分顺序运行具有更多的开销。在表面上，将程序的所有部分作为单个任务运行并节省上下文切换的成本似乎更便宜。

可以产生影响的问题是阻塞。如果你的程序中的一个任务由于程序控制之外的某些条件（通常是I/O）而无法继续，我们会说任务或线程阻塞（在我们的科幻故事中，克隆体已敲门而且是等待它打开）。如果没有并发性，整个程序就会停止，直到外部条件发生变化。但是，如果使用并发编写程序，则当一个任务被阻止时，程序中的其他任务可以继续执行，因此程序继续向前移动。实际上，从性能的角度来看，在单处理器机器上使用并发是没有意义的，除非其中一个任务可能阻塞。

单处理器系统中性能改进的一个常见例子是事件驱动编程，特别是用户界面编程。考虑一个程序执行一些长时间运行操作，从而最终忽略用户输入和无响应。如果你有一个“退出”按钮，你不想在你编写的每段代码中轮询它。这会产生笨拙的代码，无法保证程序员不会忘记执行检查。没有并发性，生成响应式用户界面的唯一方法是让所有任务定期检查用户输入。通过创建单独的执行线程来响应用户输入，该程序保证了一定程度的响应。

实现并发的直接方法是在操作系统级别，使用与线程不同的进程。进程是一个在自己的地址空间内运行的自包含程序。进程很有吸引力，因为操作系统通常将一个进程与另一个进程隔离，因此它们不会相互干扰，这使得进程编程相对容易。相比之下，线程共享内存和I/O等资源，因此编写多线程程序时遇到的困难是在不同的线程驱动的任务之间协调这些资源，一次不能通过多个任务访问它们。
<!-- 文献引用未加，因为暂时没看到很好的解决办法 -->
有些人甚至提倡将进程作为并发的唯一合理方法[^1]，但不幸的是，通常存在数量和开销限制，以防止它们在并发频谱中的适用性（最终你习惯了标准的并发性克制，“这种方法适用于一些情况但不适用于其他情况”）

一些编程语言旨在将并发任务彼此隔离。这些通常被称为_函数式语言_，其中每个函数调用不产生其他影响（因此不能与其他函数干涉），因此可以作为独立的任务来驱动。Erlang就是这样一种语言，它包括一个任务与另一个任务进行通信的安全机制。如果您发现程序的一部分必须大量使用并发性并且您在尝试构建该部分时遇到了过多的问题，那么您可能会考虑使用专用并发语言创建程序的那一部分。
<!-- 文献标记 -->
Java采用了更传统的方法[^2]，即在顺序语言之上添加对线程的支持而不是在多任务操作系统中分配外部进程，线程在执行程序所代表的单个进程中创建任务交换。

并发性会带来成本，包括复杂性成本，但可以通过程序设计，资源平衡和用户便利性的改进来抵消。通常，并发性使您能够创建更加松散耦合的设计;否则，您的代码部分将被迫明确标注通常由并发处理的操作。

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

如果您被迫进行并发，请采取最简单，最安全的方法来解决问题。使用众所周知的库并尽可能少地编写自己的代码。有了并发性，就没有“太简单了”。自负才是你的敌人。

### 2.没有什么是真的，一切可能都有问题

没有并发性的编程，你会发现你的世界有一定的顺序和一致性。通过简单地将变量赋值给某个值，很明显它应该始终正常工作。

在并发领域，有些事情可能是真的而有些事情却不是，你必须认为没有什么是真的。你必须质疑一切。即使将变量设置为某个值也可能或者可能不会按预期的方式工作，并且从那里开始走下坡路。我已经很熟悉的东西，认为它显然有效但实际上并没有。

在非并发程序中你可以忽略的各种事情突然变得非常重要。例如，您必须知道处理器缓存以及保持本地缓存与主内存一致的问题。您必须了解对象构造的深度复杂性，以便您的构造对象不会意外地将数据暴露给其他线程进行更改。问题还有很多。

虽然这些主题太复杂，无法为您提供本章的专业知识（再次参见Java Concurrency in Practice），但您必须了解它们。

### 3.它起作用,并不意味着它没有问题

您可以轻松编写一个似乎可以工作，但实际上是有问题的并发程序，并且该问题仅在最极限的条件下显示出来 - 在您部署程序后不可避免地会出现用户问题。

- 你不能证明并发程序是正确的，你只能（有时）证明它是不正确的。
- 大多数情况下你甚至不能这样做：如果它有问题，你可能无法检测到它。
- 您通常不能编写有用的测试，因此您必须依靠代码检查结合深入的并发知识来发现错误。
- 即使是有效的程序也只能在其设计参数下工作。当超出这些设计参数时，大多数并发程序会以某种方式失败。

在其他Java主题中，我们培养了一种感觉-决定论。一切都按照语言的承诺（或隐含）进行，这是令人欣慰和期待的 - 毕竟，编程语言的目的是让机器做我们想要的。从确定性编程的世界进入并发编程领域，我们遇到了一种称为[Dunning-Kruger](https://en.wikipedia.org/wiki/Dunning%E2%80%93Kruger_effect)效应的认知偏差，可以概括为“你知道的越多，你认为你知道得越多。”这意味着“......相对不熟练的人拥有着虚幻的优越感，错误地评估他们的能力远高于实际。

我自己的经验是，无论你是多么确定你的代码是线程安全的，它可能已经无效了。你可以很容易地了解所有的问题，然后几个月或几年后你会发现一些概念让你意识到你编写的大多数内容实际上都容易受到并发错误的影响。当某些内容不正确时，编译器不会告诉您。为了使它正确，你必须在研究代码时掌握前脑的所有并发问题。

在Java的所有非并发领域，“没有明显的错误和没有明显的编译错误”似乎意味着一切都好。对于并发，它没有任何意义。你可以在这个情况下做的最糟糕的事情是“自信”。

### 4.你必须仍然理解

在格言1-3之后，您可能会对并发性感到害怕，并且认为，“到目前为止，我已经避免了它，也许我可以继续保留它。

这是一种理性的反应。您可能知道其他编程语言更好地设计用于构建并发程序 - 甚至是在JVM上运行的程序（从而提供与Java的轻松通信），例如Clojure或Scala。为什么不用这些语言编写并发部分并将Java用于其他所有部分呢？

唉，你不能轻易逃脱：

- 即使你从未明确地创建一个线程，你可能使用的框架 - 例如，Swing图形用户界面（GUI）库，或者像**Timer** clas那样简单的东西。
- 这是最糟糕的事情：当您创建组件时，您必须假设这些组件可能在多线程环境中重用。即使你的解决方案是放弃并声明你的组件“不是线程安全的”，你仍然必须知道这样的声明是重要的，它是什么意思？

人们有时会认为并发性太难，不能包含在介绍该语言的书中。他们认为并发是一个可以独立对待的独立主题，并且它在日常编程中出现的少数情况（例如图形用户界面）可以用特殊的习语来处理。如果你可以避免它，为什么要介绍这样的复杂的主题。

唉，如果只是这样的话，那就太好了。但不幸的是，您无法选择何时在Java程序中出现线程。仅仅你从未写过自己的线程，并不意味着你可以避免编写线程代码。例如，Web系统是最常见的Java应用程序之一，本质上是多线程的Web服务器通常包含多个处理器，而并行性是利用这些处理器的理想方式。就像这样的系统看起来那么简单，你必须理解并发才能正确地编写它。

Java是一种多线程语言，如果您了解它们是否存在并发问题。因此，有许多Java程序正在使用中，或者只是偶然工作，或者大部分时间工作并且不时地发生问题，因为。有时这种问题是相对良性的，但有时它意味着丢失有价值的数据，如果你没有意识到并发问题，你最终可能会把问题放在其他地方而不是你的代码中。如果将程序移动到多处理器系统，则可以暴露或放大这些类型的问题。基本上，了解并发性使您意识到正确的程序可能会表现出错误的行为。

<!-- The Brutal Truth -->
## <span id = "The-Brutal-Truth">残酷的真相</span>

当人类开始烹饪他们的食物时，他们大大减少了他们的身体分解和消化食物所需的能量。烹饪创造了一个“外化的胃”，从而释放出追去其他的的能力。火的使用促成了文明。

我们现在通过计算机和网络技术创造了一个“外化大脑”，开始了第二次基本转变。虽然我们只是触及表面，但已经引发了其他转变，例如设计生物机制的能力，并且已经看到文化演变的显着加速（过去，人们不得不前往混合文化，但现在他们开始混合互联网）。这些转变的影响和好处已经超出了科幻作家预测它们的能力（他们在预测文化和个人变化，甚至技术转变的次要影响方面都特别困难）。

有了这种根本性的人类变化，看到许多破坏和失败的实验并不令人惊讶。实际上，进化依赖于无数的实验，其中大多数都失败了。这些实验是向前发展的必要条件

Java是在充满自信，热情和睿智的氛围中创建的。在发明一种编程语言时，很容易就像语言的初始可塑性会持续存在一样，你可以把某些东西拿出来，如果不能解决问题，那么就修复它。编程语言以这种方式是独一无二的 - 它们经历了类似水的改变：气态，液态和最终的固态。在气体相位期间，灵活性似乎是无限的，并且很容易认为它总是那样。一旦人们开始使用您的语言，变化就会变得更加严重，环境变得更加粘稠。语言设计的过程本身就是一门艺术。

紧迫感来自互联网的最初兴起。它似乎是一场比赛，第一个通过起跑线的人将“获胜”（事实上，Java，JavaScript和PHP等语言的流行程度可以证明这一点）。唉，通过匆忙设计语言而产生的认知负荷和技术债务最终会赶上我们。

[Turing completeness](https://en.wikipedia.org/wiki/Turing_completeness)是不足够的;语言需要更多的东西：它们必须能够创造性地表达，而不是用不必要的东西来衡量我们。解放我们的心理能力只是为了扭转并再次陷入困境，这是毫无意义的。我承认，尽管存在这些问题，我们已经完成了令人惊奇的事情，但我也知道如果没有这些问题我们能做得更多。

热情使原始Java设计师因为看起来有必要而投入功能。信心（以及原始语言的气味）让他们认为任何问题都可以解决。在时间轴的某个地方，有人认为任何加入Java的东西是固定的和永久性的 - 这是非常有信心，相信第一个决定永远是正确的，因此我们看到Java的体系中充斥着糟糕的决策。其中一些决定最终没有什么后果;例如，您可以告诉人们不要使用Vector，但保留了对之前版本的支持。

线程包含在Java 1.0中。当然，并发性是影响语言远角的基本语言设计决策，很难想象以后添加它。公平地说，当时并不清楚基本的并发性是多少。像C这样的其他语言能够将线程视为一个附加功能，因此Java设计师也纷纷效仿，包括一个Thread类和必要的JVM支持（这比你想象的要复杂得多）。

C语言是面向过程语言，这限制了它的野心。这些限制使附加线程库合理。当采用原始模型并将其粘贴到复杂语言中时，Java的大规模扩展迅速暴露了基本问题。在Thread类中的许多方法的弃用以及后续的高级库浪潮中，这种情况变得明显，这些库试图提供更好的并发抽象。

不幸的是，为了在更高级别的语言中获得并发性，所有语言功能都会受到影响，包括最基本的功能，例如标识符代表可变值。在函数和方法中，所有不变和防止副作用的方法都会导致简化并发编程（这些是纯函数式编程语言的基础）的变化，但当时对于主流语言的创建者来说似乎是奇怪的想法。最初的Java设计师要么对这些选择有所了解，要么认为它们太不同了，并且会抛弃许多潜在的语言采用者。我们可以慷慨地说，语言设计社区当时根本没有足够的经验来理解调整在线程库中的影响。

Java实验告诉我们，结果是悄然灾难性的。程序员很容易陷入认为Java 线程并不那么困难的陷阱。似乎工作的程序充满了微妙的并发bug。

为了获得正确的并发性，语言功能必须从头开始设计并考虑并发性。这艘船航行了;Java将不再是为并发而设计的语言，而只是一种允许它的语言。

尽管有这些基本的不可修复的缺陷，但令人印象深刻的是它还有多远。Java的后续版本添加了库，以便在使用并发时提升抽象级别。事实上，我根本不会想到有可能在Java 8中进行改进：并行流和**CompletableFutures** - 这是惊人的史诗般的变化，我会惊奇地重复的查看它[^3]。

这些改进非常有用，我们将在本章重点介绍并行流和**CompletableFutures**。虽然它们可以大大简化您对并发和后续代码的思考方式，但基本问题仍然存在：由于Java的原始设计，代码的所有部分仍然容易受到攻击，您仍然必须理解这些复杂和微妙的问题。Java中的线程绝不是简单或安全的;那种经历必须降级为另一种更新的语言。

<!-- The Rest of the Chapter -->
## 本章其余部分

这是我们将在本章的其余部分介绍的内容。请记住，本章的重点是使用最新的高级Java并发结构。使用这些使得您的生活比旧的替代品更加轻松。但是，您仍会在遗留代码中遇到一些低级工具。有时，你可能会被迫自己使用其中的一些。附录：[并发底层原理](./Appendix-Low-Level-Concurrency.md)包含一些更原始的Java并发元素的介绍。

- Parallel Streams（并发流）
到目前为止，我已经强调了Java 8 Streams提供的改进语法。现在您对该语法（作为一个粉丝，我希望）感到满意，您可以获得额外的好处：您可以通过简单地将parallel()添加到表达式来并行化流。这是一种简单，强大，坦率地说是利用多处理器的惊人方式

添加parallel()来提高速度似乎是微不足道的，但是，唉，它就像你刚刚在[残酷的真相](#The-Brutal-Truth)中学到的那样简单。我将演示并解释一些盲目添加parallel()到Stream表达式的缺陷。

- 创建和运行任务
任务是一段可以独立运行的代码。为了解释创建和运行任务的一些基础知识，本节介绍了一种比并行流或CompletableFutures：Executor更复杂的机制。执行者管理一些低级Thread对象（Java中最原始的并发形式）。您创建一个任务，然后将其交给Executorto运行。

有多种类型的Executor用于不同的目的。在这里，我们将展示规范形式，代表创建和运行任务的最简单和最佳方法。

- 终止长时间运行的任务
任务独立运行，因此需要一种机制来关闭它们。典型的方法使用了一个标志，这引入了共享内存的问题，我们将使用Java的“Atomic”库来回避它。
- Completable Futures
当您将衣服带到干洗店时，他们会给您一张收据。你继续完成其他任务，最终你的衣服很干净，你可以拿起它。收据是您与干洗店在后台执行的任务的连接。这是Java 5中引入的Future的方法。

Future比以前的方法更方便，但你仍然必须出现并用收据取出干洗，等待任务没有完成。对于一系列操作，Futures并没有真正帮助那么多。

Java 8 CompletableFuture是一个更好的解决方案：它允许您将操作链接在一起，因此您不必将代码写入接口排序操作。有了CompletableFuture完美的结合，就可以更容易地做出“采购原料，组合成分，烹饪食物，提供食物，清理菜肴，储存菜肴”等一系列链式操作。

- 死锁
某些任务必须去**等待 - 阻塞**来获得其他任务的结果。被阻止的任务有可能等待另一个被阻止的任务，等待另一个被阻止的任务，等等。如果被阻止的任务链循环到第一个，没有人可以取得任何进展，你就会陷入僵局。

如果在运行程序时没有立即出现死锁，则会出现最大的问题。您的系统可能容易出现死锁，并且只会在某些条件下死锁。程序可能在某个平台上运行正常，例如您的开发机器，但是当您将其部署到不同的硬件时会开始死锁。

死锁通常源于细微的编程错误;一系列无辜的决定，最终意外地创建了一个依赖循环。本节包含一个经典示例，演示了死锁的特性。

我们将通过模拟创建披萨的过程完成本章，首先使用并行流实现它，然后是完成配置。这不仅仅是两种方法的比较，更重要的是探索你应该投入多少工作来加速计划。

- 努力，复杂，成本
<!-- Parallel Streams -->
## 并行流

Java 8流的一个显着优点是，在某些情况下，它们可以很容易地并行化。这来自仔细的库设计，特别是流使用内部迭代的方式 - 也就是说，它们控制着自己的迭代器。特别是，他们使用一种特殊的迭代器，称为Spliterator，它被限制为易于自动分割。这产生了相当神奇的结果，即能够简单用parallel()然后流中的所有内容都作为一组并行任务运行。如果您的代码是使用Streams编写的，那么并行化以提高速度似乎是一种琐事

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
    /*
    Output:
    1224
    */
```

请注意，这不是微基准测试，因为我们计时整个程序。我们将数据保存在磁盘上以防止过激的优化;如果我们没有对结果做任何事情，那么一个高级的编译器可能会观察到程序没有意义并且消除了计算（这不太可能，但并非不可能）。请注意使用nio2库编写文件的简单性（在[文件](./17-Files.md)一章中有描述）。

当我注释掉[1] parallel()行时，我的结果大约是parallel()的三倍。

并行流似乎是一个甜蜜的交易。您所需要做的就是将编程问题转换为流，然后插入parallel()以加快速度。实际上，有时候这很容易。但遗憾的是，有许多陷阱。

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
        Timer timer = newTimer();
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
/* Output:5000000050000000
Sum Stream: 167ms
Sum Stream Parallel: 46ms
Sum Iterated: 284ms
*/
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
            Arrays.parallelPrefix(la, Long::sum)
        return la[la.length - 1];
        });
    }
}
/* Output:200000010000000
Array Stream
Sum: 104ms
Parallel: 81ms
Basic Sum: 106ms
parallelPrefix: 265ms
*/
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
/* Output:50000005000000
Long Array
Stream Reduce: 1038ms
Long Basic
Sum: 21ms
Long parallelPrefix: 3616ms
*/
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
/* Output:50000005000000
Long Parallel: 1014ms
*/
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

使用parallel()时会有更复杂的问题。从其他语言中吸取的流是围绕无限流模型设计的。如果您拥有有限数量的元素，则可以使用集合以及为有限大小的集合设计的关联算法。如果您使用无限流，则使用针对流优化的算法。

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
    .reduce(":", (s1, s2) -> s1 + s2)
    System.out.println(result);
    }
}
/* Output:btpen
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
*/
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
/*
Output:
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
*/
```

current是使用线程安全的**AtomicInteger**类定义的，可以防止竞争条件;**parallel()**允许多个线程调用**get()**。

在查看**PSP2.txt**。**IntGenerator.get()**被调用1024次时，您可能会感到惊讶。

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
/* Output:
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
*/
```

为了表明**parallel()**确实有效，我添加了一个对**peek()**的调用，这是一个主要用于调试的流函数：它从流中提取一个值并执行某些操作但不影响从流向下传递的元素。注意这会干扰线程行为，但我只是尝试在这里做一些事情，而不是实际调试任何东西。

您还可以看到boxed()的添加，它接受int流并将其转换为Integer流。

现在我们得到多个线程产生不同的值，但它只产生10个请求的值，而不是1024个产生10个值。

它更快吗？一个更好的问题是：什么时候开始有意义？当然不是这么小的一套;上下文切换的代价远远超过并行性的任何加速。当一个简单的数字序列并行生成时，有点难以想象。如果你使用昂贵的产品，它可能有意义 - 但这都是猜测。唯一知道的是通过测试。记住这句格言：“首先制作它，然后快速制作 - 但只有你必须这样做。”**parallel()**和**limit()**仅供专家使用（并且要清楚，我不认为自己是这里的专家）。

- 并行流只看起来很容易

实际上，在许多情况下，并行流确实可以毫不费力地更快地产生结果。但正如您所见，只需将**parallel()**打到您的Stream操作上并不一定是安全的事情。在使用**parallel()**之前，您必须了解并行性如何帮助或损害您的操作。有个错误认识是认为并行性总是一个好主意。事实上并不是。Stream意味着您不需要重写所有代码以便并行运行它。流什么都不做的是取代理解并行性如何工作的需要，以及它是否有助于实现您的目标。

<!-- Creating and Running Tasks -->
## 创建和运行任务

如果无法通过并行流实现并发，则必须创建并运行自己的任务。稍后您将看到运行任务的理想Java 8方法是CompletableFuture，但我们将使用更基本的工具介绍概念。

Java并发的历史始于非常原始和有问题的机制，并且充满了各种尝试的改进。这些主要归入附录：[低级并发(Appendix: Low-Level Concurrency)](./Appendix-Low-Level-Concurrency.md)。在这里，我们将展示一个规范形式，表示创建和运行任务的最简单，最好的方法。与并发中的所有内容一样，存在各种变体，但这些变体要么降级到该附录，要么超出本书的范围。

- Tasks and Executors

在Java的早期版本中，您通过直接创建自己的Thread对象来使用线程，甚至将它们子类化以创建您自己的特定“任务线程”对象。你手动调用了构造函数并自己启动了线程。

创建所有这些线程的开销变得非常重要，现在不鼓励采用实际操作方法。在Java 5中，添加了类来为您处理线程池。您可以将任务创建为单独的类型，然后将其交给ExecutorService以运行该任务，而不是为每种不同类型的任务创建新的Thread子类型。ExecutorService为您管理线程，并且在运行任务后重新循环线程而不是丢弃线程。

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

对**TimeUnit.MILLISECONDS.sleep()**的调用获取“当前线程”并在参数中将其置于休眠状态，这意味着该线程被挂起。这并不意味着底层处理器停止。操作系统将其切换到其他任务，例如在您的计算机上运行另一个窗口。OS任务管理器定期检查**sleep()**是否超时。当它执行时，线程被“唤醒”并给予更多处理时间。

你可以看到**sleep()**抛出一个已检查的**InterruptedException**;这是原始Java设计中的一个工件，它通过突然断开它们来终止任务。因为它往往会产生不稳定的状态，所以后来不鼓励终止。但是，我们必须在需要或仍然发生终止的情况下捕获异常。

要执行任务，我们将从最简单的方法--SingleThreadExecutor开始:

```java
/ concurrent/SingleThreadExecutor.java
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
/* Output:
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
*/
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
/* Output:
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
*/
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
/* Output:
java.util.concurrent.RejectedExecutionException: TaskNapTask[99] rejected from java.util.concurrent.ThreadPoolExecutor@4e25154f[Shutting down, pool size = 1,active threads = 1, queued tasks = 0, completed tasks =0]NapTask[1] pool-1-thread-1
*/

```

**exec.shutdown()**的替代方法是**exec.shutdownNow()**，它除了不接受新任务外，还会尝试通过中断任务来停止任何当前正在运行的任务。同样，中断是错误的，容易出错并且不鼓励。

- 使用更多线程

使用线程的重点是（几乎总是）更快地完成任务，那么我们为什么要限制自己使用SingleThreadExecutor呢？查看执行**Executors**的Javadoc，您将看到更多选项。例如CachedThreadPool：

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
/* Output:
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
*/

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
/* Output:
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
*/

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
/* Output:
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
*/

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

**ExecutorService**允许您使用**invokeAll()**启动集合中的每个Callable：

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
/* Output:
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
*/

```

只有在所有任务完成后，**invokeAll()**才会返回一个**Future**列表，每个任务一个**Future**。**Future**是Java 5中引入的机制，允许您提交任务而无需等待它完成。在这里，我们使用**ExecutorService.submit()**:

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
/* Output:
99 pool-1-thread-1 100
100
*/
```

- [1] 当你的任务尚未完成的**Future**上调用**get()**时，调用会阻塞（等待）直到结果可用。

但这意味着，在**CachedThreadPool3.java**中，**Future**似乎是多余的，因为**invokeAll()**甚至在所有任务完成之前都不会返回。但是，这里的Future并不用于延迟结果，而是用于捕获任何可能发生的异常。

还要注意在**CachedThreadPool3.java.get()**中抛出异常，因此**extractResult()**在Stream中执行此提取。

因为当你调用**get()**时，**Future**会阻塞，所以它只能解决等待任务完成的问题。最终，**Futures**被认为是一种无效的解决方案，现在不鼓励，支持Java 8的**CompletableFuture**，我们将在本章后面探讨。当然，您仍会在遗留库中遇到Futures

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
/* Output:
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
*/
```

这不仅更容易理解，我们需要做的就是将**parallel()**插入到其他顺序操作中，然后一切都在同时运行。

- Lambda和方法引用作为任务

使用lambdas和方法引用，您不仅限于使用**Runnables**和**Callables**。因为Java 8通过匹配签名来支持lambda和方法引用（即，它支持结构一致性），所以我们可以将notRunnables或Callables的参数传递给ExecutorService：

使用lambdas和方法引用，您不仅限于使用**Runnables**和**Callables**。因为Java 8通过匹配签名来支持lambda和方法引用（即，它支持结构一致性），所以我们可以将不是**Runnables**或**Callables**的参数传递给**ExecutorService**：

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
    exec.submit(newNotRunnable()::go);
    exec.submit(() -> {
        System.out.println("Lambda2");
        return 1;
    });
    exec.submit(newNotCallable()::get);
    exec.shutdown();
    }
}
/* Output:
Lambda1
NotCallable
NotRunnable
Lambda2
*/

```

这里，前两个**submit()**调用可以改为调用**execute()**。所有**submit()**调用都返回**Futures**，您可以在后两次调用的情况下提取结果。

<!-- Terminating Long-Running Tasks -->
## 终止耗时任务

并发程序通常使用长时间运行的任务。可调用任务在完成时返回值;虽然这给它一个有限的寿命，但仍然可能很长。可运行的任务有时被设置为永远运行的后台进程。您经常需要一种方法在正常完成之前停止**Runnable**和**Callable**任务，例如当您关闭程序时。

最初的Java设计提供了中断运行任务的机制（为了向后兼容，仍然存在）;中断机制包括阻塞问题。中断任务既乱又复杂，因为您必须了解可能发生中断的所有可能状态，以及可能导致的数据丢失。使用中断被视为反对模式，但我们仍然被迫接受。

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

需要**running AtomicBoolean**证明编写Java program并发时最基本的困难之一是，如果**running**是一个普通的布尔值，你可能无法在执行程序中看到问题。实际上，在这个例子中，你可能永远不会有任何问题 - 但是代码仍然是不安全的。编写表明该问题的测试可能很困难或不可能。因此，您没有任何反馈来告诉您已经做错了。通常，您编写线程安全代码的唯一方法就是通过了解事情可能出错的所有细微之处。

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
/* Output:24 27 31 8 11 7 19 12 16 4 23 3 28 32 15 20 63 60 68 6764 39 47 52 51 55 40 43 48 59 44 56 36 35 71 72 83 10396 92 88 99 100 87 91 79 75 84 76 115 108 112 104 107111 95 80 147 120 127 119 123 144 143 116 132 124 128
136 131 135 139 148 140 2 126 6 5 1 18 129 17 14 13 2122 9 10 30 33 58 37 125 26 34 133 145 78 137 141 138 6274 142 86 65 73 146 70 42 149 121 110 134 105 82 117106 113 122 45 114 118 38 50 29 90 101 89 57 53 94 4161 66 130 69 77 81 85 93 25 102 54 109 98 49 46 97
*/
```

我使用**peek()**将**QuittableTasks**传递给**ExecutorService**，然后将这些任务收集到**List.main()**中，只要任何任务仍在运行，就会阻止程序退出。即使为每个任务按顺序调用quit()方法，任务也不会按照它们创建的顺序关闭。独立运行的任务不会确定性地响应信号。

<!-- CompletableFutures -->
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
/* Output:7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 2526 27 28 29 30 31 32 33 34 6 35 4 38 39 40 41 42 43 4445 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 6263 64 65 66 67 68 69 70 71 72 73 74 75 76 77 78 79 8081 82 83 84 85 86 87 88 89 90 91 92 93 94 95 96 97 9899 100 101 102 103 104 105 106 107 108 109 110 111 1121 113 114 116 117 118 119 120 121 122 123 124 125 126127 128 129 130 131 132 133 134 135 136 137 138 139 140141 142 143 144 145 146 147 148 149 5 115 37 36 2 3*/
```

任务是一个**List <QuittableTask>**，就像在**QuittingTasks.java**中一样，但是在这个例子中，没有**peek()**将每个**QuittableTask**提交给**ExecutorService**。相反，在创建cfutures期间，每个任务都交给**CompletableFuture::runAsync**。这执行**VerifyTask.run(**)并返回**CompletableFuture <Void>**。因为**run()**不返回任何内容，所以在这种情况下我只使用**CompletableFuture**调用**join()**来等待它完成。

在此示例中需要注意的重要事项是，运行任务不需要**ExecutorService**。这由**CompletableFuture**管理（尽管有提供自己的**ExecutorService**的选项）。你也不需要调用**shutdown()**;事实上，除非你像我这样明确地调用**join()**，程序将尽快退出，而不必等待任务完成。

这个例子只是一个起点。你很快就会看到ComplempleFutures能够做得更多。

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
        System.out.println(m);return m;
    }
    @Override
    public StringtoString() {
        return"Machina" + id + ": " +      (state.equals(State.END)? "complete" : state);
    }
}

```

这是一个有限状态机，一个微不足道的机器，因为它没有分支......它只是从头到尾遍历一条路径。**work()**方法将机器从一个状态移动到下一个状态，并且需要100毫秒才能完成“工作”。

我们可以用**CompletableFuture**做的一件事是使用**completedFuture()**将它包装在感兴趣的对象中

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

当我们将Machina包装在CompletableFuture中时，我们发现我们可以在CompletableFuture上添加操作来处理所包含的对象，事情变得更加有趣：

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
/* Output:
Machina0: ONE
Machina0: TWO
Machina0: THREE
Machina0: complete
*/

```

**thenApply()**应用一个接受输入并产生输出的函数。在这种情况下，**work()**函数产生与它相同的类型，因此每个得到的**CompletableFuture**仍然被输入为**Machina**，但是（类似于**Streams**中的**map()**）**Function**也可以返回不同的类型，这将反映在返回类型

您可以在此处看到有关CompletableFutures的重要信息：它们会在您执行操作时自动解包并重新包装它们所携带的对象。这样你就不会陷入麻烦的细节，这使得编写和理解代码变得更加简单。

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
/* Output:
Machina0: ONE
Machina0: TWO
Machina0: THREE
Machina0: complete
514
*/

```

在这里，我们还添加了一个**Timer**，它向我们展示每一步增加100毫秒，还有一些额外的开销。
**CompletableFutures**的一个重要好处是它们鼓励使用私有子类原则（不分享任何东西）。默认情况下，使用**thenApply()**来应用一个不与任何人通信的函数 - 它只需要一个参数并返回一个结果。这是函数式编程的基础，并且它在并发性方面非常有效[^5]。并行流和ComplempleFutures旨在支持这些原则。只要您不决定共享数据（共享非常容易，甚至意外）您可以编写相对安全的并发程序。

回调**thenApply()**开始一个操作，在这种情况下，在完成所有任务之前，不会完成**e CompletableFuture**的创建。虽然这有时很有用，但是启动所有任务通常更有价值，这样就可以运行时继续前进并执行其他操作。我们通过在操作结束时添加Async来实现此目的：

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
            System.out.println(timer.duration())
    }
}
/* Output:
116
Machina0: ONE
Machina0: TWO
Machina0:THREE
Machina0: complete
Machina0: complete
552
*/
```

同步调用(我们通常使用得那种)意味着“当你完成工作时，返回”，而异步调用以意味着“立刻返回但是继续后台工作。”正如你所看到的，**cf**的创建现在发生得跟快。每次调用 **thenApplyAsync()** 都会立刻返回，因此可以进行下一次调用，整个链接序列的完成速度比以前快得快。

事实上，如果没有回调**cf.join() t**方法，程序会在完成其工作之前退出（尝试取出该行）对**join()**阻止了main()进程的进行，直到cf操作完成，我们可以看到大部分时间的确在哪里度过。

这种“立即返回”的异步能力需要**CompletableFuture**库进行一些秘密工作。特别是，它必须将您需要的操作链存储为一组回调。当第一个后台操作完成并返回时，第二个后台操作必须获取生成的**Machina**并开始工作，当完成后，下一个操作将接管，等等。但是没有我们普通的函数调用序列，通过程序调用栈控制，这个顺序会丢失，所以它使用回调 - 一个函数地址表来存储。

幸运的是，您需要了解有关回调的所有信息。程序员将你手工造成的混乱称为“回调地狱”。通过异步调用，CompletableFuture为您管理所有回调。除非你知道关于你的系统有什么特定的改变，否则你可能想要使用异步调用。

- 其他操作
当您查看CompletableFuture的Javadoc时，您会看到它有很多方法，但这个方法的大部分来自不同操作的变体。例如，有thenApply()，thenApplyAsync()和thenApplyAsync()的第二种形式，它接受运行任务的Executor（在本书中我们忽略了Executor选项）。

这是一个显示所有“基本”操作的示例，它们不涉及组合两个CompletableFutures或异常（我们将在稍后查看）。首先，我们将重复使用两个实用程序以提供简洁和方便：

```java
// concurrent/CompletableUtilities.java
package onjava; import java.util.concurrent.*;
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

showr()在CompletableFuture <Integer>上调用get()并显示结果，捕获两个可能的异常。voidr()是CompletableFuture <Void>的showr()版本，即CompletableFutures，仅在任务完成或失败时显示。

为简单起见，以下CompletableFutures只包装整数。cfi()是一个方便的方法，它在完成的CompletableFuture <Integer>中包装一个int：

```java
// concurrent/CompletableOperations.java
import java.util.concurrent.*;
import static onjava.CompletableUtilities.*;
public class CompletableOperations {
    static CompletableFuture<Integer> cfi(int i) {
        return CompletableFuture.completedFuture( Integer.valueOf(i));
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
        System.out.println("cancelled: " + c.isCancelled());
        System.out.println("completed exceptionally: " +
            c.isCompletedExceptionally());
        System.out.println("done: " + c.isDone());
        System.out.println(c);
        c = new CompletableFuture<>();
        System.out.println(c.getNow(777));
        c = new CompletableFuture<>();
        c.thenApplyAsync(i -> i + 42)
            .thenApplyAsync(i -> i * 12);
        System.out.println("dependents: " + c.getNumberOfDependents());
        c.thenApplyAsync(i -> i / 2);
        System.out.println("dependents: " + c.getNumberOfDependents());
    }
}
/* Output:
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
*/
```

main()包含一系列可由其int值引用的测试。cfi(1)演示了showr()正常工作。cfi(2)是调用runAsync()的示例。由于Runnable不产生返回值，因此结果是CompletableFuture <Void>，因此使用voidr()。
<!-- Deadlock -->
## 死锁



<!-- Constructors are not Thread-Safe -->
## 构造函数非线程安全


<!-- Effort, Complexity,Cost -->
## 复杂性和代价


<!-- Summary -->
## 本章小结

[^1]:例如,Eric-Raymond在“VIIX编程艺术”（Addison-Wesley，2004）中提出了一个很好的案例。

[^2]:可以说，试图将并发性用于后续语言是一种注定要失败的方法，但你必须得出自己的结论

[^3]:有人谈论在Java——10中围绕泛型做一些类似的基本改进，这将是非常令人难以置信的。
[^4]:这是一种有趣的，虽然不一致的方法。通常，我们期望在公共接口上使用显式类表示不同的行为
[^5]:不，永远不会有纯粹的功能性Java。我们所能期望的最好的是一种在JVM上运行的全新语言。

<!-- 分页 -->
<div style="page-break-after: always;"></div>