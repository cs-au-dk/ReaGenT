package dk.webbies.tajscheck.tajstester.monitors;

import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.Function;
import dk.brics.tajs.flowgraph.SourceLocation;
import dk.brics.tajs.flowgraph.jsnodes.BinaryOperatorNode;
import dk.brics.tajs.flowgraph.jsnodes.ConstantNode;
import dk.brics.tajs.flowgraph.jsnodes.DeclareFunctionNode;
import dk.brics.tajs.flowgraph.jsnodes.DefaultNodeVisitor;
import dk.brics.tajs.flowgraph.jsnodes.EndForInNode;
import dk.brics.tajs.flowgraph.jsnodes.EndLoopNode;
import dk.brics.tajs.flowgraph.jsnodes.EndTAJSSplitBlockNode;
import dk.brics.tajs.flowgraph.jsnodes.EndWithNode;
import dk.brics.tajs.flowgraph.jsnodes.EventDispatcherNode;
import dk.brics.tajs.flowgraph.jsnodes.ExceptionalReturnNode;
import dk.brics.tajs.flowgraph.jsnodes.HasNextPropertyNode;
import dk.brics.tajs.flowgraph.jsnodes.TypeofNode;
import dk.brics.tajs.lattice.CallEdge;
import dk.brics.tajs.lattice.Context;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.monitoring.AnalysisPhase;
import dk.brics.tajs.monitoring.DefaultAnalysisMonitoring;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.solver.GenericSolver;
import dk.brics.tajs.util.Collectors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TajsCoverageResult extends DefaultAnalysisMonitoring {

    private Map<AbstractNode, Integer> branches = new HashMap<>();
    private Map<AbstractNode, Integer> statements = new HashMap<>();
    private Map<AbstractNode, Integer> functions = new HashMap<>();
    private HashSet<AbstractNode> covered = new HashSet<>();

    private GenericSolver<State, Context, CallEdge, IAnalysisMonitoring, Analysis>.SolverInterface c;

    public double statementCoverage() {
        return (statementCount() / (statements.size() * 1.0));
    }

    private long statementCount() {
        return statements.values().stream().filter(n -> n > 0).count();
    }

    public double functionCoverage() {
        return (functionCount() / (functions.size() * 1.0));
    }

    private long functionCount() {
        return functions.values().stream().filter(n -> n > 0).count();
    }

    public double branchCoverage() {
        return (branchCount() * 1.0) / branches.size();
    }

    private long branchCount() {
        return functions.values().stream().filter(n -> n > 0).count();
    }

    @Override
    public void setSolverInterface(GenericSolver<State, Context, CallEdge, IAnalysisMonitoring, Analysis>.SolverInterface c) {
        this.c = c;
    }

    @Override
    public void visitPhasePost(AnalysisPhase phase) {
        if(phase == AnalysisPhase.SCAN) {
            // all functions of all loaded source code files
            Collection<Function> fgFunctions = c.getFlowGraph().getFunctions();
            for (Function function : fgFunctions) {
                if (isInUserLocation(function.getSourceLocation())) {

                    // function coverage
                    AbstractNode functionNode = function.getEntry().getFirstNode();
                    functions.put(functionNode, coverage(functionNode));

                    for (BasicBlock b : function.getBlocks()) {
                        for (AbstractNode n : b.getNodes()) {
                            if (!n.isArtificial()) {
                                if (StatementRecognizer.isStatement(n)) {

                                    // statement coverage
                                    statements.put(n, coverage(n));

                                    // branches coverage
                                    Set<AbstractNode> branchesToCover = branchesToCover(n);
                                    if (branchesToCover != null && branchesToCover.size() > 0) { // it's a branch-statement
                                        int covered = branchesToCover
                                                .stream()
                                                .map(this::coverage)
                                                .mapToInt(Integer::intValue)
                                                .min()
                                                .getAsInt();
                                        branches.put(n, covered);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.c = null; //GC come in
            covered = null;
        }
    }

    private int coverage(AbstractNode n) {
        if(covered.contains(n)) return 1; //Binary, not quantified across contexts
        return 0;
    }

    private boolean isInUserLocation(SourceLocation l) {
        return !c.getFlowGraph().isHostEnvironmentSource(l) && l.getLocation() != null;
    }

    @Override
    public void visitNodeTransferPost(AbstractNode n, State s) {
        if(!s.isBottom()) {
            covered.add(n);
        }
    }

    private static class StatementRecognizer extends DefaultNodeVisitor {
        private boolean outcome = true;

        public static boolean isStatement(AbstractNode n) {
            StatementRecognizer o = new StatementRecognizer();
            n.visitBy(o);
            return o.outcome;
        }

        @Override
        public void visit(ConstantNode n) {
            outcome = false;
        }
        @Override
        public void visit(ExceptionalReturnNode n) {
            outcome = false;
        }

        @Override
        public void visit(DeclareFunctionNode n) {
            outcome = false;
        }

        @Override
        public void visit(EndWithNode n) {
            outcome = false;
        }

        @Override
        public void visit(HasNextPropertyNode n) {
            outcome = false;
        }

        @Override
        public void visit(TypeofNode n) {
            outcome = false;
        }

        @Override
        public void visit(BinaryOperatorNode n) {
            outcome = false;
        }

        @Override
        public void visit(EventDispatcherNode n) {
            outcome = false;
        }

        @Override
        public void visit(EndForInNode n) {
            outcome = false;
        }

        @Override
        public void visit(EndLoopNode n) {
            outcome = false;
        }

        @Override
        public void visit(EndTAJSSplitBlockNode n) {
            outcome = false;
        }
    }

    public Set<AbstractNode> branchesToCover(AbstractNode n) {
        if(n.getBlock().getLastNode() == n && n.getBlock().getSuccessors().size() > 1) {
            return n.getBlock().getSuccessors().stream().map(BasicBlock::getFirstNode).collect(Collectors.toSet());
        }
        return null;
    }

}
