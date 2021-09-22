package Software.SistemaOperacional;

import java.util.ArrayList;

import Hardware.Memory;
import Hardware.Memory.Word;

public class MemoryManager{

        protected Memory memory;
        private int pageLength;
        private int frameLength;
        private int nFrames;
        private boolean freeFrames[];

        public MemoryManager(Memory _m, int _pLength){
            memory = _m;
            pageLength = _pLength;
            frameLength = pageLength;
            nFrames = memory.address.length/pageLength;

            freeFrames = new boolean[nFrames];
            for(int i = 0; i < freeFrames.length; i++){
                freeFrames[i] = true;
            }
        }

        public class AllocatesReturn{
            boolean canAlocate;
            int[] tablePages;
        }

        public AllocatesReturn allocates(int nWords){
            AllocatesReturn allocatesReturn = new AllocatesReturn();
            int nrFrames;
            ArrayList<Integer> aux = new ArrayList<Integer>();

            if (nWords % pageLength > 0){ // Com offset
                nrFrames = (nWords / pageLength) + 1;
            } else {
                nrFrames = (nWords / pageLength);
            }

            //first fit
            for(int i = 0; i < freeFrames.length; i++){
                if(nrFrames > 0){
                    if(freeFrames[i]){
                        aux.add(i);
                        freeFrames[i] = false;
                        nrFrames--;
                    }
                } else break;
            }

            allocatesReturn.tablePages = aux.stream().mapToInt(i -> i).toArray();

            if(nrFrames > 0){ 
                dislocate(allocatesReturn.tablePages);
                allocatesReturn.canAlocate = false;
            }else{
                allocatesReturn.canAlocate = true;
            }

            return allocatesReturn;
        }

        public void dislocate(int[] tablePages){
            for (int page : tablePages) {
                freeFrames[page] = true;
            }
        }

        public void carga(Word[] program, int[] tablePages) {
            int countWords = 0;
            for (int i : tablePages) {
                for (int j = i * frameLength ; j < ((i+1) * frameLength) - 1 ; j++) {
                    while (countWords < frameLength) {
                        if(countWords <= program.length){
                            memory.address[j] = program[countWords];
                            countWords++;
                        } else break;
                    }
                }
            }
            
        }

    }
