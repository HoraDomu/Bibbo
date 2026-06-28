// Run with: cargo run --bin seed
// Deletes bibbo.db and creates 50 interconnected test nodes.
use rusqlite::Connection;

fn main() {
    let _ = std::fs::remove_file("bibbo.db");
    let db = Connection::open("bibbo.db").expect("open db");

    db.execute_batch(
        "CREATE TABLE IF NOT EXISTS nodes (
            id        INTEGER PRIMARY KEY AUTOINCREMENT,
            title     TEXT    NOT NULL,
            body      TEXT    NOT NULL,
            color_idx INTEGER NOT NULL,
            pos_x     REAL    NOT NULL,
            pos_y     REAL    NOT NULL,
            created   TEXT    NOT NULL
        );
        CREATE TABLE IF NOT EXISTS edges (
            id        INTEGER PRIMARY KEY AUTOINCREMENT,
            source_id INTEGER NOT NULL,
            target_id INTEGER NOT NULL,
            UNIQUE(source_id, target_id)
        );",
    ).unwrap();

    let nodes: &[(&str, &str)] = &[
        // AI / ML cluster
        ("Machine Learning",     "A field of AI where systems learn from data without being explicitly programmed. Core ideas: [[Neural Networks]], [[Training Data]], [[Gradient Descent]], [[Overfitting]]."),
        ("Neural Networks",      "Layers of artificial neurons that learn representations. Built on [[Backpropagation]] and [[Activation Functions]]. The backbone of [[Deep Learning]]."),
        ("Deep Learning",        "Neural networks with many layers. Powers [[Computer Vision]], [[Natural Language Processing]], and [[Reinforcement Learning]]. Requires lots of [[Training Data]]."),
        ("Gradient Descent",     "Optimization algorithm that minimizes a [[Loss Function]] by following the slope downhill. Used in [[Machine Learning]] and [[Neural Networks]]."),
        ("Backpropagation",      "Algorithm for computing gradients in [[Neural Networks]] using the [[Chain Rule]] from [[Calculus]]. Enables [[Gradient Descent]] to train deep models."),
        ("Overfitting",          "When a model memorizes [[Training Data]] instead of generalizing. Prevented with regularization, dropout, and more [[Training Data]]."),
        ("Training Data",        "The labeled examples used to train [[Machine Learning]] models. Quality matters more than quantity. Affects [[Overfitting]] and model performance."),
        ("Loss Function",        "Measures how wrong a model is. [[Gradient Descent]] minimizes it. Common examples: cross-entropy, MSE. Central to [[Machine Learning]]."),
        ("Activation Functions", "Non-linearities in [[Neural Networks]] like ReLU and sigmoid. Without them networks can only learn linear mappings."),
        ("Reinforcement Learning","An agent learns by trial and error, maximizing reward. Combines [[Machine Learning]] with [[Game Theory]]. Behind AlphaGo and GPT fine-tuning."),
        ("Computer Vision",      "Teaching machines to see. Built on [[Deep Learning]] and [[Convolutional Networks]]. Applied in self-driving cars, medical imaging."),
        ("Natural Language Processing", "Teaching machines to understand text. Powered by [[Transformers]] and [[Deep Learning]]. Enables search, translation, summarization."),
        ("Transformers",         "Architecture behind modern [[Natural Language Processing]]. Uses attention instead of recurrence. Basis for GPT, BERT, and [[Large Language Models]]."),
        ("Large Language Models","Massive [[Transformers]] trained on internet-scale text. Examples: GPT-4, Claude. Enable [[Natural Language Processing]] at human-level quality."),
        ("Convolutional Networks","Neural network architecture for images. Basis of [[Computer Vision]]. Uses shared filters to detect local patterns."),

        // Math cluster
        ("Calculus",             "The mathematics of change. [[Derivatives]] and [[Integrals]] form its core. Essential for [[Gradient Descent]] and [[Backpropagation]]."),
        ("Derivatives",          "Measures the rate of change of a function. Core of [[Calculus]]. The [[Chain Rule]] chains them together for [[Backpropagation]]."),
        ("Integrals",            "Accumulates change over an interval. Inverse of [[Derivatives]]. Core of [[Calculus]]. Used in probability and [[Statistics]]."),
        ("Chain Rule",           "Rule for differentiating composed functions. From [[Calculus]]. The mathematical engine behind [[Backpropagation]]."),
        ("Statistics",           "The science of data. Provides tools for [[Machine Learning]], [[Probability]], and [[Bayesian Inference]]."),
        ("Probability",          "Quantifies uncertainty. Foundation of [[Statistics]], [[Bayesian Inference]], and [[Reinforcement Learning]]."),
        ("Bayesian Inference",   "Updates beliefs as new evidence arrives. Rooted in [[Probability]] and [[Statistics]]. Alternative to frequentist thinking."),
        ("Linear Algebra",       "The mathematics of vectors and matrices. Every [[Neural Networks]] forward pass is matrix multiplication. Tied to [[Calculus]] and [[Statistics]]."),
        ("Game Theory",          "Studies strategic interactions between rational agents. Underpins [[Reinforcement Learning]] and economics. Linked to [[Probability]]."),

        // Science cluster
        ("Neuroscience",         "Studies the brain. Inspired [[Neural Networks]] and [[Deep Learning]]. Concepts like attention, memory, and plasticity map to AI architectures."),
        ("Evolution",            "Change in species over time through [[Natural Selection]]. A metaphor for [[Reinforcement Learning]] and genetic algorithms."),
        ("Natural Selection",    "Mechanism of [[Evolution]]. Fittest variants survive and reproduce. Inspires evolutionary algorithms in [[Machine Learning]]."),
        ("Quantum Computing",    "Uses quantum superposition to compute. May accelerate [[Machine Learning]] and break current cryptography. Grounded in [[Physics]]."),
        ("Physics",              "Describes the fundamental rules of reality. Underpins [[Quantum Computing]] and cosmology. Linked to [[Calculus]] and [[Linear Algebra]]."),
        ("Information Theory",   "Shannon's framework for measuring information and entropy. Underpins [[Natural Language Processing]], [[Loss Function]] design, and [[Statistics]]."),

        // Philosophy / Thinking cluster
        ("First Principles",     "Break a problem down to its irreducible truths, then reason up. Opposed to reasoning by analogy. Used by Aristotle and Elon Musk."),
        ("Mental Models",        "Frameworks for understanding reality. Examples: [[First Principles]], inversion, [[Occam's Razor]]. The building blocks of clear thinking."),
        ("Occam's Razor",        "Prefer the simplest explanation that fits the data. Related to [[Bayesian Inference]] and [[Mental Models]]."),
        ("Emergence",            "Complex behavior arising from simple rules. [[Neural Networks]] exhibit emergence. Seen in [[Evolution]], ant colonies, and cities."),
        ("Systems Thinking",     "Understanding how parts interact to form a whole. Prevents local optimizations that hurt [[Emergence]] at the system level."),
        ("Cognitive Bias",       "Systematic errors in thinking. Understanding them is part of [[Mental Models]]. Studied in [[Behavioral Economics]]."),
        ("Behavioral Economics", "Merges psychology with economics. Shows humans deviate from [[Game Theory]] predictions. Rooted in [[Cognitive Bias]] research."),

        // Technology / Product cluster
        ("Software Architecture","High-level structure of software systems. Good architecture enables [[Emergence]] of features and resists [[Technical Debt]]."),
        ("Technical Debt",       "The cost of shortcuts in code. Accumulates when speed beats quality. Managed through refactoring and [[Software Architecture]]."),
        ("Distributed Systems",  "Software running across multiple machines. Hard problems: consensus, latency, fault tolerance. Requires [[Systems Thinking]]."),
        ("Cryptography",         "Securing information with mathematics. Under threat from [[Quantum Computing]]. Relies on [[Number Theory]] and [[Probability]]."),
        ("Number Theory",        "Study of integers and primes. Basis of [[Cryptography]]. Connects to [[Linear Algebra]] and [[Statistics]]."),

        // Knowledge / Learning cluster
        ("Spaced Repetition",    "Review information at expanding intervals to maximize retention. Grounded in [[Memory]] research and [[Neuroscience]]."),
        ("Memory",               "How the brain stores and retrieves information. Central to [[Neuroscience]] and [[Spaced Repetition]]. Affected by sleep and emotion."),
        ("Flow State",           "Deep focus where time disappears. Enabled by clear goals, immediate feedback, and challenge matching skill. Studied in [[Neuroscience]]."),
        ("Second Brain",         "An external system for capturing and connecting ideas. Built from [[Mental Models]], [[Spaced Repetition]], and linking like [[Natural Language Processing]] links concepts."),
        ("Writing",              "Thinking made visible. Clarifies ideas through [[First Principles]] reasoning. Powers [[Second Brain]] and knowledge transfer."),
        ("Reading",              "Absorbing others' thinking. Input for [[Second Brain]]. Pairs with [[Spaced Repetition]] for retention. Related to [[Memory]]."),
        ("Creativity",           "Generating novel ideas by combining existing ones in new ways. Linked to [[Flow State]], [[Writing]], and [[Emergence]]."),
        ("Focus",                "Directing attention intentionally. Required for [[Flow State]], [[Deep Learning]] of any subject, and [[Writing]]."),
    ];

    // Spread nodes in a rough cluster pattern
    let total = nodes.len() as f32;
    for (i, (title, body)) in nodes.iter().enumerate() {
        let angle = (i as f32 / total) * std::f32::consts::TAU;
        let tier = (i / 15) as f32;
        let radius = 280.0 + tier * 120.0;
        // Add jitter
        let h = (i as u64).wrapping_mul(6364136223846793005);
        let jx = ((h & 0xFF) as f32 / 255.0 - 0.5) * 80.0;
        let jy = (((h >> 8) & 0xFF) as f32 / 255.0 - 0.5) * 80.0;
        let x = angle.cos() * radius + jx;
        let y = angle.sin() * radius + jy;
        let ci = i % 8;

        db.execute(
            "INSERT INTO nodes (title, body, color_idx, pos_x, pos_y, created) VALUES (?1,?2,?3,?4,?5,'June 28, 2026')",
            rusqlite::params![title, body, ci as i64, x as f64, y as f64],
        ).unwrap();
    }

    // Build edges: scan each node's body for [[links]], match to other node titles
    let all_nodes: Vec<(i64, String, String)> = {
        let mut s = db.prepare("SELECT id, title, body FROM nodes").unwrap();
        s.query_map([], |r| Ok((r.get(0)?, r.get(1)?, r.get(2)?))).unwrap()
            .filter_map(|r| r.ok()).collect()
    };

    fn norm(s: &str) -> String {
        s.split_whitespace().collect::<Vec<_>>().join(" ").to_lowercase()
    }
    fn parse_links(body: &str) -> Vec<String> {
        let mut links = Vec::new();
        let mut rest = body;
        while let Some(s) = rest.find("[[") {
            rest = &rest[s + 2..];
            if let Some(e) = rest.find("]]") {
                links.push(rest[..e].trim().to_string());
                rest = &rest[e + 2..];
            } else { break; }
        }
        links
    }

    for (src_id, _, body) in &all_nodes {
        for tag in parse_links(body) {
            let tn = norm(&tag);
            if let Some((tgt_id, _, _)) = all_nodes.iter().find(|(id, title, _)| *id != *src_id && norm(title) == tn) {
                let _ = db.execute(
                    "INSERT OR IGNORE INTO edges (source_id, target_id) VALUES (?1, ?2)",
                    rusqlite::params![src_id, tgt_id],
                );
            }
        }
    }

    let node_count: i64 = db.query_row("SELECT COUNT(*) FROM nodes", [], |r| r.get(0)).unwrap();
    let edge_count: i64 = db.query_row("SELECT COUNT(*) FROM edges", [], |r| r.get(0)).unwrap();
    println!("Seeded {} nodes, {} edges → bibbo.db", node_count, edge_count);
}
