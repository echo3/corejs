package nextapp.coredoc.parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nextapp.coredoc.model.Block;
import nextapp.coredoc.model.Bounds;
import nextapp.coredoc.model.CoreExtendBlock;
import nextapp.coredoc.model.DocComment;
import nextapp.coredoc.model.FunctionBlock;
import nextapp.coredoc.model.Instance;
import nextapp.coredoc.model.Module;
import nextapp.coredoc.model.ObjectBlock;
import nextapp.coredoc.model.FieldBlock;
import nextapp.coredoc.util.Patterns;

public class ModuleParser {

    public static Module parse(Instance instance, String source) {
        return new ModuleParser(instance, source).parse();
    }
    
    private Map startIndexToBounds;
    private Map boundsToBlock = new TreeMap();
    private String source;
    private String safeSource;
    
    private Module module;
    
    private Set blockStartIndices = new TreeSet();
    
    private ModuleParser(Instance instance, String source) {
        super();
        this.source = source;
        this.safeSource = SourceTextMasker.getSafeSource(source);
        module = new Module(instance);
    }
    
    private void assembleStructure() {
        Iterator it = boundsToBlock.values().iterator();
        while (it.hasNext()) {
            Block block = (Block) it.next();

            if (block.getName().equals(block.getDeclaredName())) {
                if (block.getBounds().getParent() == null) {
                    // Block is top-level.
                    module.addStructureChild(block);
                } else {
                    Block parentBlock = (Block) boundsToBlock.get(block.getBounds().getParent());
                    if (parentBlock != null) {
                        parentBlock.addStructureChild(block);
                    }
                }
            } else {
                if (block.getBounds().getParent() == null) {
                    String declaredName = block.getDeclaredName();
                    StringTokenizer nameTokenizer = new StringTokenizer(declaredName, ".");
                    Block parentBlock = module.getStructureChild(nameTokenizer.nextToken().trim());
                    if (parentBlock == null) {
                        throw new RuntimeException("Cannot find parent for: " + declaredName);
                    }
                    while (nameTokenizer.hasMoreTokens()) {
                        String name = nameTokenizer.nextToken().trim();
                        if (nameTokenizer.hasMoreTokens()) { // Do not process last token.
                            parentBlock = parentBlock.getStructureChild(name);
                            if (parentBlock == null) {
                                throw new RuntimeException("Cannot find parent for: " + name);
                            }
                        }
                    }
                    parentBlock.addStructureChild(block);
                } else {
                    //FIXME
                    //System.err.println(block.getDeclaredName());
                }
            }
        }
    }
    
    private void createBracePairMap() {
        startIndexToBounds = new TreeMap();
        Matcher braceMatcher = Patterns.braces.matcher(safeSource);
        List openBracePairs = new ArrayList();
        
        while (braceMatcher.find()) {
            char braceChar = braceMatcher.group().charAt(0);
            switch (braceChar) {
            case '{':
                openBracePairs.add(new Bounds(openBracePairs.size() == 0 
                        ? null : ((Bounds) openBracePairs.get(openBracePairs.size() - 1)), braceMatcher.start(), -1));
                break;
            case '}':
                if (openBracePairs.size() == 0) {
                    throw new RuntimeException("Closing brace without opening brace, position: " + braceMatcher.start());
                }
                Bounds bracePair = (Bounds) openBracePairs.remove(openBracePairs.size() - 1);
                bracePair.setEndIndex(braceMatcher.end());
                startIndexToBounds.put(new Integer(bracePair.getStartIndex()), bracePair);
                break;
            default:
                throw new RuntimeException();
            }
        }
        if (openBracePairs.size() > 0) {
            throw new RuntimeException("Closing brace without opening brace, position.");
        }
    }
    
    private void createFunctionBlocks() {
        Matcher functionDeclarationMatcher = Patterns.functionDeclaration.matcher(safeSource);
        while (functionDeclarationMatcher.find()) {
            Bounds bounds = (Bounds) startIndexToBounds.get(new Integer(functionDeclarationMatcher.end() - 1));
            bounds.setStartIndex(functionDeclarationMatcher.start());
            FunctionBlock block = new FunctionBlock(module, bounds);
            block.setDeclaredName(functionDeclarationMatcher.group(1));
            boundsToBlock.put(bounds, block);
        }

        Matcher functionAssignmentMatcher = Patterns.functionAssignment.matcher(safeSource);
        while (functionAssignmentMatcher.find()) {
            if (!markBlockLoaded(functionAssignmentMatcher.start())) {
                continue;
            }
            Bounds bounds = (Bounds) startIndexToBounds.get(new Integer(functionAssignmentMatcher.end() - 1));
            bounds.setStartIndex(functionAssignmentMatcher.start());
            FunctionBlock block = new FunctionBlock(module, bounds);
            block.setDeclaredName(functionAssignmentMatcher.group(1));
            boundsToBlock.put(bounds, block);
        }
    }
    
    private void createFieldBlocks() {
        Matcher propertyAssignmentMatcher = Patterns.fieldAssignment.matcher(safeSource);
        Map fieldMap = new TreeMap();
        while (propertyAssignmentMatcher.find()) {
            if (!markBlockLoaded(propertyAssignmentMatcher.start())) {
                continue;
            }
            Bounds bounds = new Bounds(null, propertyAssignmentMatcher.start(), propertyAssignmentMatcher.end());
            FieldBlock block = new FieldBlock(module, bounds);
            block.setDeclaredName(propertyAssignmentMatcher.group(1));
            Block parent = findParent(bounds.getStartIndex());
            if (parent == null) {
                continue;
            }
            bounds.setParent(parent.getBounds());
            fieldMap.put(bounds, block);
        }
        boundsToBlock.putAll(fieldMap);
    }
    
    private boolean markBlockLoaded(int startIndex) {
        if (blockStartIndices.contains(Integer.valueOf(startIndex))) {
            return false;
        } else {
            blockStartIndices.add(Integer.valueOf(startIndex));
            return true;
        }
    }
    
    private void createCoreExtendBlocks() {
        Matcher coreExtendMatcher = Patterns.coreExtend.matcher(safeSource);
        while (coreExtendMatcher.find()) {
            if (!markBlockLoaded(coreExtendMatcher.start())) {
                continue;
            }
            Bounds bounds = (Bounds) startIndexToBounds.get(new Integer(coreExtendMatcher.end() - 1));
            bounds.setStartIndex(coreExtendMatcher.start());
            CoreExtendBlock block = new CoreExtendBlock(module, bounds);
            block.setDeclaredName(coreExtendMatcher.group(1));
            block.setSuperclassName(coreExtendMatcher.group(2));
            boundsToBlock.put(bounds, block);
        }
    }
    
    private void createObjectBlocks() {
        Matcher objectLiteralAssignmentMatcher = Patterns.objectLiteralAssignment.matcher(safeSource);
        while (objectLiteralAssignmentMatcher.find()) {
            if (!markBlockLoaded(objectLiteralAssignmentMatcher.start())) {
                continue;
            }
            Bounds bounds = (Bounds) startIndexToBounds.get(new Integer(objectLiteralAssignmentMatcher.end() - 1));
            bounds.setStartIndex(objectLiteralAssignmentMatcher.start());
            ObjectBlock block = new ObjectBlock(module, bounds);            
            block.setDeclaredName(objectLiteralAssignmentMatcher.group(1));
            boundsToBlock.put(bounds, block);
        }
    }

    private void attachDocComments() {
        Pattern nonWhitespace = Pattern.compile("\\S", Pattern.MULTILINE);
        Matcher matcher = Patterns.docComment.matcher(source);
        while (matcher.find()) {
            String text = matcher.group();
            int end = matcher.end();
            Matcher nonWhitespaceMatcher = nonWhitespace.matcher(source);
            nonWhitespaceMatcher.find(end);
            int blockStart = nonWhitespaceMatcher.start();
            Block block = findChild(blockStart);
            if (block != null) {
                DocComment docComment = DocCommentParser.parse(text);
                block.setDocComment(docComment);
            }
        }
    }
    
    private Block findChild(int startIndex) {
        Iterator it = boundsToBlock.values().iterator();
        while (it.hasNext()) {
            Block block = (Block) it.next();
            if (block.getBounds().getStartIndex() == startIndex) {
                return block;
            }
            block = block.findChild(startIndex);
            if (block != null) {
                return block;
            }
        }
        return null;
    }
    
    private Block findParent(int startIndex) {
        Iterator it = boundsToBlock.values().iterator();
        Block lastBlock = null;
        while (it.hasNext()) {
            Block block = (Block) it.next();
            if (block.getBounds().getStartIndex() > startIndex) {
                return lastBlock;
            }
            lastBlock = block;
        }
        return lastBlock;
    }

    private Module parse() {
        createBracePairMap();
        
        createFunctionBlocks();
        createCoreExtendBlocks();
        createObjectBlocks();
        createFieldBlocks();
        
        assembleStructure();
        
        attachDocComments();
        
        return module;
    }    
}
